package Segundo_Entregable;


import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;

public class RestServer extends AbstractVerticle {

	private Map<Integer, SensorEntity> sensores = new HashMap<Integer, SensorEntity>();
	private Map<Integer, ActuadorEntity> actuadores= new HashMap<Integer, ActuadorEntity>();
	private Gson gson;

	public void start(Promise<Void> startFuture) {
		// Creating some synthetic data
		createSomeDataSensores(25);
		createSomeDataActuadores(25);

		// Instantiating a Gson serialize object using specific date format
		gson = new GsonBuilder().setDateFormat("yyyy-MM-dd").create();

		// Defining the router object
		Router router = Router.router(vertx);

		// Handling any server startup result
		vertx.createHttpServer().requestHandler(router::handle).listen(8080, result -> {
			if (result.succeeded()) {
				startFuture.complete();
			} else {
				startFuture.fail(result.cause());
			}
		});
		
		//Ruta para los sensores
		router.route("/api/sensores*").handler(BodyHandler.create());
		router.get("/api/sensores").handler(this::getAllSensores);
		router.get("/api/sensores/3registros").handler(this::getLastThreeSensores);
		router.get("/api/sensores/registros/:sensorId").handler(this::LastSensorValue);
		router.get("/api/sensores/:sensorId").handler(this::getOneSensor);
		router.post("/api/sensores").handler(this::addOneSensor);
		router.delete("/api/sensores/:sensorId").handler(this::deleteOneSensor);
		//Ruta para los actuadores
		router.route("/api/actuadores*").handler(BodyHandler.create());
		router.get("/api/actuadores").handler(this::getAllActuadores);
		router.get("/api/actuadores/3registros").handler(this::getLastThreeActuadores);
		router.get("/api/actuadores/registros/:actuadorId").handler(this::LastActuadorValue);
		router.get("/api/actuadores/:actuadorId").handler(this::getOneActuador);
		router.post("/api/actuadores").handler(this::addOneActuador);
		router.delete("/api/actuadores/:actuadorId").handler(this::deleteOneActuador);
		//Ruta para el idGroup
		router.route("/api/groups*").handler(BodyHandler.create());
		router.get("/api/groups/:idGroup").handler(this::getIdGroup);
	}

	private void getAllSensores(RoutingContext routingContext) {
		routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
		.end(gson.toJson(sensores.values()));
	}

	private void getAllActuadores(RoutingContext routingContext) {
		routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
		.end(gson.toJson(actuadores.values()));
	}	
	
	private void getOneSensor(RoutingContext routingContext) {
		int sensorId = Integer.parseInt(routingContext.request().getParam("sensorId"));
		if (sensores.containsKey(sensorId)) {
			SensorEntity se =sensores.get(sensorId);
			routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
			.end(gson.toJson(se));
		} else {
			routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(500)
			.end();
		}
	}

	private void getOneActuador(RoutingContext routingContext) {
		int actuadorId = Integer.parseInt(routingContext.request().getParam("actuadorId"));
		if (actuadores.containsKey(actuadorId)) {
			ActuadorEntity se = actuadores.get(actuadorId);
			routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
			.end(gson.toJson(se));
		} else {
			routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(500)
			.end();
		}
	}

	private void getLastThreeActuadores(RoutingContext routingContext) {
		// Obtener todas las entradas del mapa de actuadores
		List<Map.Entry<Integer, ActuadorEntity>> entries = new ArrayList<>(actuadores.entrySet());

		// Verificar si hay menos de tres elementos
		if (entries.size() <= 3) {
			// Si hay tres o menos elementos, devolver todas las entradas
			routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
			.end(gson.toJson(entries));
		} else {
			// Si hay más de tres elementos, obtener los últimos tres elementos
			List<Map.Entry<Integer, ActuadorEntity>> lastThreeEntries = entries.subList(entries.size() - 3, entries.size());
			// Convertir las entradas de mapa a una lista de valores (en este caso, los ActuadorEntity)
			List<ActuadorEntity> lastThreeActuadores = lastThreeEntries.stream()
					.map(Map.Entry::getValue)
					.collect(Collectors.toList());

			// Devolver los últimos tres actuadores como respuesta
			routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
			.end(gson.toJson(lastThreeActuadores));
		}
	}

	private void getLastThreeSensores(RoutingContext routingContext) {
		// Obtener todas las entradas del mapa de actuadores
		List<Map.Entry<Integer, SensorEntity>> entries = new ArrayList<>(sensores.entrySet());

		// Verifico si hay menos de tres elementos
		if (entries.size() <= 3) {
			// Si hay tres o menos elementos, devuelvo todas las entradas
			routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
			.end(gson.toJson(entries));
		} else {
			// Si hay más de tres elementos, obtengo los últimos tres elementos
			List<Map.Entry<Integer, SensorEntity>> lastThreeEntries = entries.subList(entries.size() - 3, entries.size());

			List<SensorEntity> lastThreeActuadores = lastThreeEntries.stream()
					.map(Map.Entry::getValue)
					.collect(Collectors.toList());

			// Devuelvo los últimos tres actuadores como respuesta
			routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
			.end(gson.toJson(lastThreeActuadores));
		}
	}
	
	private void LastSensorValue(RoutingContext routingContext) {
	    int sensorId = Integer.parseInt(routingContext.request().getParam("sensorId"));
	    List<SensorEntity> sen = sensores.values()
	            .stream()
	            .sorted(Comparator.comparing(SensorEntity::getTimeStamp))
	            .filter(x -> x.getSensorId().equals(sensorId))
	            .collect(Collectors.toList());

	    List<Double> values = sen.stream()
	            .map(SensorEntity::getValues)
	            .map(Double::doubleValue)
	            .collect(Collectors.toList());

	    routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
	            .end(gson.toJson(values));
	}

	
	
	private void LastActuadorValue(RoutingContext routingContext) {
		int actuadorId = Integer.parseInt(routingContext.request().getParam("actuadorId"));
		List<ActuadorEntity> sen = actuadores.values()
                .stream()
                .sorted(Comparator.comparing(ActuadorEntity::getTimeStamp))
                .filter(x->x.getActuadorId().equals(actuadorId))
                .collect(Collectors.toList());
		
		List<Boolean> activo= sen.stream().map(ActuadorEntity::getActivo)
				.map(Boolean::booleanValue)
				.collect(Collectors.toList());

		routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
		.end(gson.toJson(activo));
		
	}

	private void addOneActuador(RoutingContext routingContext) {
		final ActuadorEntity actuador = gson.fromJson(routingContext.getBodyAsString(), ActuadorEntity.class);
		actuadores.put(actuador.getPlacaId(),actuador);
		routingContext.response().setStatusCode(201).putHeader("content-type", "application/json; charset=utf-8")
		.end(gson.toJson(actuador));
	}

	private void addOneSensor(RoutingContext routingContext) {
		final SensorEntity sensor = gson.fromJson(routingContext.getBodyAsString(), SensorEntity.class);
		sensores.put(sensor.getPlacaId(),sensor);
		routingContext.response().setStatusCode(201).putHeader("content-type", "application/json; charset=utf-8")
		.end(gson.toJson(sensor));
	}

	private void deleteOneActuador (RoutingContext routingContext) {
		int actuadorId = Integer.parseInt(routingContext.request().getParam("actuadorId"));
		if (actuadores.containsKey(actuadorId)) {
			ActuadorEntity actuador = actuadores.get(actuadorId);
			actuadores.remove(actuadorId);
			routingContext.response().setStatusCode(200).putHeader("content-type", "application/json; charset=utf-8")
			.end(gson.toJson(actuador));
		} else {
			routingContext.response().setStatusCode(204).putHeader("content-type", "application/json; charset=utf-8")
			.end();
		}
	}
	
	private void deleteOneSensor (RoutingContext routingContext) {
		int sensorId = Integer.parseInt(routingContext.request().getParam("sensorId"));
		if (sensores.containsKey(sensorId)) {
			SensorEntity sensor = sensores.get(sensorId);
			sensores.remove(sensorId);
			routingContext.response().setStatusCode(200).putHeader("content-type", "application/json; charset=utf-8")
			.end(gson.toJson(sensor));
		} else {
			routingContext.response().setStatusCode(204).putHeader("content-type", "application/json; charset=utf-8")
			.end();
		}
	}
	
	private void getIdGroup(RoutingContext routingContext) {
		List<Object> lista_sensores_actuadores= new ArrayList<>();
		int idGroup = Integer.parseInt(routingContext.request().getParam("idGroup"));
		if (sensores.containsKey(idGroup) && actuadores.containsKey(idGroup)) {
			SensorEntity se =sensores.get(idGroup);
			ActuadorEntity ac = actuadores.get(idGroup);
			lista_sensores_actuadores.add(se);
			lista_sensores_actuadores.add(ac);
			routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(200)
			.end(gson.toJson(lista_sensores_actuadores));
		} else {
			routingContext.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(500)
			.end();
		}
	}

	private void createSomeDataSensores(int number) {
		Random rnd = new Random();
		IntStream.range(0, number).forEach(elem -> {
			int id = rnd.nextInt();
			if(id>0) {
				sensores.put(id, new SensorEntity(id/4,id, id, id*3.0,(long)id*4));
			}
		});
		sensores.put(1, new SensorEntity(1,1, 1, 1.0, (long)1));
	}
	private void createSomeDataActuadores(int number) {
		Random rnd = new Random();
		IntStream.range(0, number).forEach(elem -> {
			int id = rnd.nextInt();
			if(id>0) {
				actuadores.put(id, new ActuadorEntity(id/4,id,id,true,(long)id*2));
			}
			
		});
		actuadores.put(1, new ActuadorEntity(1, 1, 1, true, (long)1));
		
		
	}

}
