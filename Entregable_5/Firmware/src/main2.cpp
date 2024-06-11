#include <HTTPClient.h>
#include "ArduinoJson.h"
#include <WiFiUdp.h>
#include <PubSubClient.h>
#include "stdio.h"
#include "WiFi.h"
#include <NTPClient.h>

int test_delay = 1000; // so we don't spam the API
boolean describe_tests = true;
const int DEVICE_ID = 1;
const int GROUP_ID = 3;

String serverName = "http://192.168.1.131:8080/";
HTTPClient http;

#define STASSID "DIGIFIBRA-C6CX" // Nombre de la red
#define STAPSK "CPUySXEsbzsb"    // contraseña

char topic[20];

// MQTT configuration
WiFiClient espClient;
PubSubClient client(espClient);

void GET_tests();
void POST_tests();
String serializeActuador(int valueId, int groupId, int placaId, int actuadorId, bool activo, String timeStamp, String nombreActuador);
String serializePlaca(int placaId, int groupId);

// Server IP, where de MQTT broker is deployed

const char *MQTT_BROKER_ADRESS = "192.168.1.131";
const uint16_t MQTT_PORT = 1883;

// Name for this MQTT client

const char *MQTT_CLIENT_NAME = "ESP32-Yuns";

const u_int8_t led = GPIO_NUM_13;
const u_int8_t ultrasonidoTrigger = GPIO_NUM_26;
const u_int8_t ultrasonidoEcho = GPIO_NUM_32;
const u_int8_t LDR = GPIO_NUM_35;

// SERVIDOR PARA EL TIEMPO

WiFiUDP ntpUDP;
NTPClient timeClient(ntpUDP, "es.pool.ntp.org", 7200);

String formattedTime;
time_t epochTime;
struct tm *ptm;
int spaceIndex;
String dateStr;
char dateStringBuff[20];
String hora;

bool estadoAnteriorLed = false;

void OnMqttReceived(char *topic, byte *payload, unsigned int length)
{
  Serial.print("Received on ");
  Serial.print(topic);
  Serial.print(": ");

  DynamicJsonDocument doc(ESP.getMaxAllocHeap());
  DeserializationError error = deserializeJson(doc, payload);

  if (error)
  {
    Serial.print("Error al deserializar JSON: ");
    Serial.println(error.c_str());
    return;
  }

  float brillo = doc["brillo"];

  int umbralLDR = 4096 * 0.01;                     // Calculamos el umbral del sensor LDR con su valor máximo, 4096(pin análogico de 12 bits)
  int umbralPWM = map(umbralLDR, 0, 4095, 0, 255); // Mapeamos el umbral al brillo de nuestro LED para decidir si se apaga o se enciende

  bool estadoLed = (brillo > umbralPWM); // True si está encendido, false eoc

  // Sólo haremos POST a la BBDD si el estado del LED cambia

  if (estadoLed != estadoAnteriorLed)
  {
     String timestamp = String(dateStringBuff) + "T" + hora;
    String ledSerializado = serializeActuador(0, GROUP_ID, DEVICE_ID, 1, estadoLed,timestamp, "LED");
    Serial.println("Test POST actuador nuevo");
    String serverPath = serverName + "api/actuadores/post";
    http.begin(serverPath.c_str());
    http.POST(ledSerializado);
    Serial.println("----------------ESTADO ANTERIOR LED--------------------");
    Serial.println(estadoAnteriorLed);
  }

  // // Actualizamos el estado del LED
  if (estadoLed)
  {
    analogWrite(led, brillo);
    Serial.println("LED encendido");
  }
  else
  {
    analogWrite(led, LOW);
    Serial.println("LED apagado");
  }

  // El estado actual pasa a ser el anterior
  estadoAnteriorLed = estadoLed;

  // 7. Imprimir información de depuración (opcional)
  Serial.println("------------BRILLO DEL LED---------");
  Serial.println(brillo);
}

void InitMqtt()
{
  client.setServer(MQTT_BROKER_ADRESS, MQTT_PORT);

  client.setCallback(OnMqttReceived);
}

void setup()
{

  sprintf(topic, "Group_%d", GROUP_ID);

  pinMode(LDR, INPUT);
  pinMode(led, OUTPUT);

  analogWrite(led, LOW);

  Serial.begin(9600);
  Serial.println();
  Serial.print("Connecting to ");
  Serial.println(STASSID);
  WiFi.mode(WIFI_STA);
  WiFi.begin(STASSID, STAPSK);

  while (WiFi.status() != WL_CONNECTED)
  {
    delay(500);
    Serial.print(".");
  }

  timeClient.begin(); // Iniciamos el servidor NTP

  InitMqtt();

  Serial.println("");
  Serial.println("WiFi connected");
  Serial.println("IP address: ");
  Serial.println(WiFi.localIP());

  // GET_tests();
  // Queremos que los objetos se inserten una ÚNICA vez
  // POST_tests();

  String body = serializePlaca(DEVICE_ID, GROUP_ID);
  String path = serverName + "api/placas/" + String(GROUP_ID);
  http.begin(path.c_str());
  http.POST(body);
  Serial.println(body);

  Serial.println("Setup!");
}

void ConnectMqtt()
{
  Serial.print("Starting MQTT connection...");
  if (client.connect(MQTT_CLIENT_NAME))
  {
    Serial.println("Conectado");
    client.publish("Prueba", MQTT_CLIENT_NAME);
    client.subscribe(topic);
  }
  else
  {
    Serial.print("Failed MQTT connection, rc=");
    Serial.print(client.state());
    Serial.println(" try again in 5 seconds");

    delay(5000);
  }
}

void HandleMqtt()
{
  if (!client.connected())
  {
    ConnectMqtt();
  }

  client.loop();
}

/*-------------PLACA----------------*/

// AGRUPAR EN MQTT

String serializePlaca(int placaId, int groupId)
{

  DynamicJsonDocument doc(ESP.getMaxAllocHeap());

  doc["placaId"] = placaId;
  doc["groupId"] = groupId;

  String output;
  serializeJson(doc, output);
  return output;
}

void deserializePlaca(int httpResponseCode)
{

  if (httpResponseCode > 0)
  {

    Serial.print("HTTP Response code: ");
    Serial.println(httpResponseCode);
    String responseJson = http.getString();
    DynamicJsonDocument doc(ESP.getMaxAllocHeap());

    DeserializationError error = deserializeJson(doc, responseJson);

    if (error)
    {
      Serial.print(F("deserializeJson() failed: "));
      Serial.println(error.f_str());
      return;
    }

    JsonArray array = doc.as<JsonArray>();
    for (JsonObject placa : array)
    {
      int valueId = placa["valueId"];
      int placaId = placa["placaId"];
      int groupId = placa["groupId"];

      Serial.println((
                         "Placa: [valueId: " + String(valueId) +
                         ", placaId: " + String(placaId) +
                         ", groupId: " + String(groupId) +
                         "]")
                         .c_str());
    }
  }
  else
  {
    Serial.print("Error code: ");
    Serial.println(httpResponseCode);
  }
}

/*---------------SENSORES--------------------*/

String serializeSensor(int valueId, int groupId, int placaId, int sensorId, float value, String timeStamp, String nombre)
{

  DynamicJsonDocument doc(2048);

  doc["valueId"] = valueId;
  doc["groupId"] = groupId;
  doc["placaId"] = placaId;
  doc["sensorId"] = sensorId;
  doc["value"] = value;
  doc["timeStamp"] = timeStamp;
  doc["nombre"] = nombre;

  String output;
  serializeJson(doc, output);
  Serial.println(output);

  return output;
}

void deserializeSensorsFromDevice(int httpResponseCode)
{
  if (httpResponseCode > 0)
  {
    Serial.print("HTTP Response code: ");
    Serial.println(httpResponseCode);
    String responseJson = http.getString();
    DynamicJsonDocument doc(ESP.getMaxAllocHeap());

    DeserializationError error = deserializeJson(doc, responseJson);

    if (error)
    {
      Serial.print(F("deserializeJson() failed: "));
      Serial.println(error.f_str());
      return;
    }

    JsonArray array = doc.as<JsonArray>();

    for (JsonObject sensor : array)
    {

      int valueId = sensor["valueId"];
      int groupId = sensor["groupId"];
      int placaId = sensor["placaId"];
      int sensorId = sensor["sensorId"];
      float valorLDR = sensor["value"];
      String timeStamp = sensor["timeStamp"];
      String nombreSensor = sensor["nombre"];

      Serial.println(
          ("Sensor deserialized: [valueId: " + String(valueId) +
           ", groupId: " + String(groupId) +
           ", placaId: " + String(placaId) +
           ", sensorId: " + String(sensorId) +
           ", valorLDR: " + String(valorLDR) +
           ", timeStamp:" + timeStamp +
           ", nombreSensor:" + nombreSensor + "]")
              .c_str());
    }
  }
  else
  {
    Serial.print("Error code: ");
    Serial.println(httpResponseCode);
  }
}

// DESERIALIZO EL ESTADO ACTUAL DEL SENSOR

void deserializeSensorStatus(String responseJson)
{
  if (responseJson != "")
  {
    DynamicJsonDocument doc(2048);

    DeserializationError error = deserializeJson(doc, responseJson);

    if (error)
    {
      Serial.print(F("deserializeJson() failed: "));
      Serial.println(error.f_str());
      return;
    }

    int groupId = doc["groupId"];
    int placaId = doc["placaId"];
    int sensorId = doc["sensorId"];
    float valor = doc["valor"];
    String timeStamp = doc["timeStamp"];
    String nombre = doc["nombre"];

    Serial.println(("Sensor status deserialized: [groupId: " + String(groupId) +
                    ", placaId: " + String(placaId) +
                    ", sensorId: " + String(sensorId) +
                    ", values" + String(valor) +
                    ", timeStamp: " + String(timeStamp) +
                    ", nombreActuador: " + String(nombre) + "]")
                       .c_str());
  }
}

/*---------------ACTUADORES--------------------*/

String serializeActuador(int valueId, int groupId, int placaId, int actuadorId, bool activo, String timeStamp, String nombreActuador)
{

  DynamicJsonDocument doc(2048);

  doc["valueId"] = valueId;
  doc["groupId"] = groupId;
  doc["placaId"] = placaId;
  doc["actuadorId"] = actuadorId;
  doc["activo"] = activo;
  doc["timeStamp"] = timeStamp;
  doc["nombreActuador"] = nombreActuador;

  String output;
  serializeJson(doc, output);
  Serial.println(output);

  return output;
}

void deserializeActuatorsFromDevice(int httpResponseCode)
{

  String ultimoActuador = "";

  if (httpResponseCode > 0)
  {
    Serial.print("HTTP Response code: ");
    Serial.println(httpResponseCode);
    String responseJson = http.getString();

    DynamicJsonDocument doc(ESP.getMaxAllocHeap());

    DeserializationError error = deserializeJson(doc, responseJson);

    if (error)
    {
      Serial.print(F("deserializeJson() failed: "));
      Serial.println(error.f_str());
      return;
    }

    JsonArray array = doc.as<JsonArray>();
    for (JsonObject actuador : array)
    {
      int valueId = actuador["valueId"];
      int groupId = actuador["groupId"];
      int placaId = actuador["placaId"];
      int actuadorId = actuador["actuadorId"];
      bool activo = actuador["activo"];
      String timeStamp = actuador["timeStamp"];
      String nombre = actuador["nombreActuador"];

      // SÓLO LO SACO POR CONSOLA SI NO HAY REPETICIÓN
      if (ultimoActuador != nombre)
      {

        ultimoActuador = nombre;

        Serial.println(
            ("Actuator deserialized: [valueId: " + String(valueId) + ", groupId: " + String(groupId) +
             ", placaId: " + String(placaId) + ", actuadorId: " + String(actuadorId) +
             ", activo: " + String(activo) +
             ", timeStamp: " + String(timeStamp) + ", nombreActuador: " + String(nombre) + "]")
                .c_str());
      }
    }
  }
  else
  {
    Serial.print("Error code: ");
    Serial.println(httpResponseCode);
  }
}

void deserializeActuadorStatus(String responseJson)
{
  if (responseJson != "")
  {
    DynamicJsonDocument doc(2048);
    DeserializationError error = deserializeJson(doc, responseJson);

    if (error)
    {
      Serial.print(F("deserializeJson() failed: "));
      Serial.println(error.f_str());
      return;
    }

    int groupId = doc["groupId"];
    int placaId = doc["placaId"];
    int actuadorId = doc["actuadorId"];
    bool activo = doc["activo"];
    String timeStamp = doc["timeStamp"];
    String nombre = doc["nombre"];

    Serial.println(("Actuator status deserialized: [groupId: " + String(groupId) +
                    ", placaId: " + String(placaId) +
                    ", actuadorId: " + String(actuadorId) +
                    ", activo" + String(activo) +
                    ", timeStamp: " + String(timeStamp) +
                    ", nombreActuador: " + String(nombre) + "]")
                       .c_str());
  }
}

// AQUÍ EMPIEZAN LOS TESTS
/*
  String response;

  void test_status(int statusCode)
  {
    delay(test_delay);
    if (statusCode == 200 || statusCode == 201)
    {
      Serial.print("TEST RESULT: ok (");
      Serial.print(statusCode);
      Serial.println(")");
    }
    else
    {
      Serial.print("TEST RESULT: fail (");
      Serial.print(statusCode);
      Serial.println(")");
    }
  }

  void test_response()
  {
    Serial.println("TEST RESULT: (response body = " + response + ")");
    response = "";
  }

  void describe(char *description)
  {
    if (describe_tests)
      Serial.println(description);
  }

  void GET_tests()
  {

    // OBTENER UN SENSOR POR ID

    delay(1000);

    Serial.println("SENSOR A PARTIR DE SENSORID");
    String serverPath = serverName + "api/sensores/" + String(DEVICE_ID);
    http.begin(serverPath.c_str());
    deserializeSensorsFromDevice(http.GET());

    delay(1000);

    String serverPath;
    Serial.println("ACTUADOR A PARTIR DE actuadorId");
    serverPath = serverName + "api/actuadores/" + String(DEVICE_ID);
    http.begin(serverPath.c_str());
    deserializeActuatorsFromDevice(http.GET());

    delay(1000);
    describe("Test GET last sensor from timeStamp");
    String serverPath = serverName + "api/sensores/registros/";
    http.begin(serverPath.c_str());
    deserializeSensorsFromDevice(http.GET());

    delay(1000);

    describe("Test GET last actuator from timeStamp");
    String serverPath = serverName + "api/actuadores/registros/";
    http.begin(serverPath.c_str());
    deserializeActuatorsFromDevice(http.GET());
  }*/

void POST_tests()
{

  String sensorPost = serializeSensor(4, 3, 2, 5, 9.8, "2024-06-06 01:13:22", "LDR");
  Serial.println("Test POST sensor nuevo");
  String serverPath = serverName + "api/sensores/post";
  http.begin(serverPath.c_str());
  http.POST(sensorPost);

  String actuadorPost = serializeActuador(4, 3, 2, 5, false, "2024-06-06 01:34:59", "LED");
  Serial.println("Test POST actuador nuevo");
  String path = serverName + "api/actuadores/post";
  http.begin(path.c_str());
  http.POST(actuadorPost);
}

// Run the tests!
void loop()
{

  timeClient.update(); // Actualizamos el tiempo cada
  formattedTime = timeClient.getFormattedTime();
  epochTime = timeClient.getEpochTime();
  ptm = localtime(&epochTime);
  spaceIndex = formattedTime.indexOf(' ');
  dateStr = formattedTime.substring(5, spaceIndex);
  strftime(dateStringBuff, sizeof(dateStringBuff), "%Y-%m-%d", ptm);
  hora = formattedTime.substring(formattedTime.indexOf('T') + 1, formattedTime.indexOf('Z')); 

  /* GET_tests();
   delay(5000);

   POST_tests();
   delay(5000);
*/

  HandleMqtt();
  delay(5000);

  // delay(10000);

  // GET_tests();

  // HandleMqtt();
  // delay(5000);

  client.loop();
  delay(5000);

  // Serial.println("-----------------VALOR LDR EN FLOAT---------------");
  float valorLDR = analogRead(LDR);
  // Serial.println(valorLDR);

  // Serial.println("-------------VALOR TAL CUAL EN LOOP---------");
  // Serial.println(analogRead(LDR));

  String LDRSerializado = serializeSensor(0, GROUP_ID, DEVICE_ID, 2, valorLDR, String(dateStringBuff) + "T" + formattedTime.substring(spaceIndex + 1), "LDR");
  Serial.println("Test POST sensor nuevo");
  String serverPath = serverName + "api/sensores/post";
  http.begin(serverPath.c_str());
  http.POST(LDRSerializado);
  delay(1000);

  HandleMqtt();
}