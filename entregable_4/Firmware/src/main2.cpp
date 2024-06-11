#include <HTTPClient.h>
#include "ArduinoJson.h"
#include <WiFiUdp.h>
#include "stdio.h"
#include "WiFi.h"

int test_delay = 1000; // so we don't spam the API
boolean describe_tests = true;
const int DEVICE_ID = 1;

String serverName = "http://192.168.1.131:8080/";
HTTPClient http;

#define STASSID "DIGIFIBRA-C6CX" // Nombre de la red
#define STAPSK "CPUySXEsbzsb"    // contraseña


void GET_tests();
void POST_tests();
String serializeActuador(int valueId, int groupId, int placaId, int actuadorId, bool activo, String timeStamp, String nombreActuador);
String serializePlaca(int placaId, int groupId);


void setup()
{

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

  Serial.println("");
  Serial.println("WiFi connected");
  Serial.println("IP address: ");
  Serial.println(WiFi.localIP());

  GET_tests();
  // Queremos que los objetos se inserten una ÚNICA vez
  POST_tests();

  Serial.println("Setup!");
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

    Serial.println("ACTUADOR A PARTIR DE actuadorId");
    serverPath = serverName + "api/actuadores/" + String(DEVICE_ID);
    http.begin(serverPath.c_str());
    deserializeActuatorsFromDevice(http.GET());

    delay(1000);
    describe("Test GET last sensor from timeStamp");
    serverPath = serverName + "api/sensores/registros/";
    http.begin(serverPath.c_str());
    deserializeSensorsFromDevice(http.GET());

    delay(1000);

    describe("Test GET last actuator from timeStamp");
    serverPath = serverName + "api/actuadores/registros/";
    http.begin(serverPath.c_str());
    deserializeActuatorsFromDevice(http.GET());
  }

void POST_tests()
{

  String sensorPost = serializeSensor(4, 3, 2, 5, 9.8, "2024-06-06 01:13:22", "LDR");
  Serial.println("Test POST sensor nuevo");
  String path2 = serverName + "api/sensores/post";
  http.begin(path2.c_str());
  http.POST(sensorPost);

  String actuadorPost = serializeActuador(4, 3, 2, 5, false, "2024-06-06 01:34:59", "LED");
  Serial.println("Test POST actuador nuevo");
  String path = serverName + "api/actuadores/post";
  http.begin(path.c_str());
  http.POST(actuadorPost);
}

void loop()
{
 
  GET_tests();
  delay(5000);

  POST_tests();
  delay(5000);

  delay(5000);

  GET_tests();



}