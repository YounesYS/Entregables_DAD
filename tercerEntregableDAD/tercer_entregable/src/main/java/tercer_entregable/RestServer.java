package tercer_entregable;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.mysqlclient.MySQLConnectOptions;
import io.vertx.mysqlclient.MySQLPool;
import io.vertx.sqlclient.PoolOptions;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;

public class RestServer extends AbstractVerticle {

	MySQLPool SQLClient;

	public void start(Promise<Void> startFuture) {

		MySQLConnectOptions connectOptions = new MySQLConnectOptions().setPort(3306).setHost("localhost")
				.setDatabase("entregable3").setUser("daduser").setPassword("iissi$user");

		PoolOptions poolOptions = new PoolOptions().setMaxSize(5); // Número de conexiones en paralelo CONTRA LA BBDD

		SQLClient = MySQLPool.pool(vertx, connectOptions, poolOptions);

		Router router = Router.router(vertx);

		vertx.createHttpServer().requestHandler(router::handle).listen(8080, result -> {
			if (result.succeeded()) {
				startFuture.complete();
			} else {
				startFuture.fail(result.cause());
			}
		});

		// PARTE DE LA API

		// Ruta para los sensores

		router.route("/api/sensores*").handler(BodyHandler.create());
		router.get("/api/sensores").handler(this::getAllSensores); 
		router.get("/api/sensores/3registros").handler(this::getLastThreeSensors);
		router.get("/api/sensores/registros/").handler(this::lastSensorValue); 
		router.get("/api/sensores/:sensorId").handler(this::getOneSensor);
		router.post("/api/sensores/post").handler(this::setNewSensor);
		router.delete("/api/sensores/delete/:sensorId").handler(this::deleteOneSensor);

		// Ruta para los actuadores

		router.route("/api/actuadores*").handler(BodyHandler.create());
		router.get("/api/actuadores").handler(this::getAllActuadores); 
		router.get("/api/actuadores/3registros").handler(this::getLastThreeActuadores); 
		router.get("/api/actuadores/registros/").handler(this::lastActuadorValue); 
		router.get("/api/actuadores/:actuadorId").handler(this::getOneActuador); 
		router.post("/api/actuadores/post").handler(this::setNewActuador);
		router.delete("/api/actuadores/delete/:actuadorId").handler(this::deleteOneActuador); 

		// setNewSensor(2,1,1,4,Float.valueOf("3.4"),"2024-04-21","LDR");
		// setNewActuador(0,1,1,2,true,"2024-04-21","Relé");
		// setNewSensor(4,1,1,7,Float.valueOf(30),"2024-04-23", "Ultrasonido");

	}

	// SELECT

	private void getOneSensor(RoutingContext rc) {

		SQLClient.getConnection(connection -> {

			if (connection.succeeded()) {

				connection.result().query(
						"SELECT * FROM entregable3.sensores where sensorId = " + rc.request().getParam("sensorId"),
						x -> {

							RowSet<Row> resultado = x.result();
							JsonArray a = new JsonArray();

							for (Row r : resultado) {

								a.add(JsonObject
										.mapFrom(new SensorEntity(r.getInteger("valueId"), r.getInteger("groupId"),
												r.getInteger("placaId"), r.getInteger("sensorId"), r.getFloat("valor"),
												r.getLocalDateTime("fechaHora")
														.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
												r.getString("nombre")

										)));

							}

							rc.response().setStatusCode(200).putHeader("content-type", "application/json")
									.end(a.encodePrettily());
						});
						connection.result().close();

			} else {

				rc.response().setStatusCode(400).putHeader("content-type", "application/json")
						.end(JsonObject.mapFrom(connection.cause()).encodePrettily());
			}

		});
	}

	private void getOneActuador(RoutingContext rc) {

		SQLClient.getConnection(connection -> {

			if (connection.succeeded()) {

				connection.result().query("SELECT * FROM entregable3.actuadores where actuadorId = "
						+ rc.request().getParam("actuadorId"), x -> {

							RowSet<Row> resultado = x.result();
							JsonArray a = new JsonArray();

							for (Row r : resultado) {
								System.out.println(r);
								a.add(JsonObject.mapFrom(new ActuadorEntity(r.getInteger("valueId"),
										r.getInteger("groupId"), 
										r.getInteger("placaId"), 
										r.getInteger("actuadorId"),
										r.getBoolean("activo"),
										r.getLocalDateTime("fechaHora")
												.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
										r.getString("nombre"))));

							}

							rc.response().setStatusCode(200).putHeader("content-type", "application/json")
									.end(a.encodePrettily());
						});
						connection.result().close();

			} else {

				rc.response().setStatusCode(400).putHeader("content-type", "application/json")
						.end(JsonObject.mapFrom(connection.cause()).encodePrettily());
			}

		});
	}

	private void getAllSensores(RoutingContext rc) {

		SQLClient.getConnection(connection -> {

			if (connection.succeeded()) {

				connection.result().query("SELECT * FROM entregable3.sensores;", x -> {

					if (x.succeeded()) {

						RowSet<Row> resultado = x.result();
						System.out.println("-------TODOS LOS SENSORES-------");
						System.out.println("");
						System.out.println("Número de sensores: " + resultado.size());
						JsonArray a = new JsonArray();

						for (Row r : resultado) {

							a.add(JsonObject.mapFrom(new SensorEntity(
									r.getInteger("valueId"), r.getInteger("groupId"), r.getInteger("placaId"),
									r.getInteger("sensorId"), r.getFloat("valor"), r.getLocalDateTime("fechaHora")
											.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
									r.getString("nombre")

							)));

						}

						rc.response().setStatusCode(200).putHeader("content-type", "application/json")
								.end(a.encodePrettily());

					} else {

						rc.response().setStatusCode(400).putHeader("content-type", "application/json")
								.end(JsonObject.mapFrom(connection.cause()).encodePrettily());
					}

					connection.result().close();

				});

			} else {

				System.out.println(connection.cause().toString());

			}
		});
	}

	private void getAllActuadores(RoutingContext rc) {

		SQLClient.getConnection(connection -> {

			if (connection.succeeded()) {

				connection.result().query("SELECT * FROM entregable3.actuadores;", x -> {

					if (x.succeeded()) {

						RowSet<Row> resultado = x.result();
						JsonArray a = new JsonArray();

						for (Row r : resultado) {

							a.add(JsonObject.mapFrom(new ActuadorEntity(
									r.getInteger("valueId"), r.getInteger("groupId"), r.getInteger("placaId"),
									r.getInteger("actuadorId"), r.getBoolean("activo"), r.getLocalDateTime("fechaHora")
											.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
									r.getString("nombre")

							)));

						}

						System.out.println("------TODOS LOS ACTUADORES------");
						System.out.println("");
						System.out.println("Número de actuadores: " + resultado.size());

						rc.response().setStatusCode(200).putHeader("content-type", "application/json")
								.end(a.encodePrettily());

					} else {

						rc.response().setStatusCode(400).putHeader("content-type", "application/json")
								.end(JsonObject.mapFrom(connection.cause()).encodePrettily());
					}

					connection.result().close();

				});

			} else {

				System.out.println(connection.cause().toString());

			}
		});
	}

	private void getAllLeds(RoutingContext rc) {

		SQLClient.getConnection(connection -> {

			if (connection.succeeded()) {

				connection.result().query("SELECT * FROM entregable3.actuadores;", x -> {

					if (x.succeeded()) {

						RowSet<Row> resultado = x.result();
						JsonArray a = new JsonArray();
						Integer contador = 0;
						System.out.println("------TODOS LOS LEDS------");
						System.out.println("");

						for (Row r : resultado) {

							if (r.getString("nombre").equals("LED")) {

								contador += 1;
								System.out.println("Número de LEDS: " + contador);

								a.add(JsonObject.mapFrom(new ActuadorEntity(r.getInteger("valueId"),
										r.getInteger("groupId"), r.getInteger("placaId"), r.getInteger("actuadorId"),
										r.getBoolean("activo"),
										r.getLocalDateTime("fechaHora")
												.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
										r.getString("nombre")

								)));

							}

							else {

								continue;
							}

						}

						System.out.println("");

						rc.response().setStatusCode(200).putHeader("content-type", "application/json")
								.end(a.encodePrettily());

					} else {

						rc.response().setStatusCode(400).putHeader("content-type", "application/json")
								.end((JsonObject.mapFrom(x.cause()).encodePrettily()));

					}

					connection.result().close();

				});

			} else {

				System.out.println(connection.cause().toString());

			}
		});

	}

	private void getAllRelays(RoutingContext rc) {

		SQLClient.getConnection(connection -> {

			if (connection.succeeded()) {

				connection.result().query("SELECT * FROM entregable3.actuadores;", x -> {

					if (x.succeeded()) {

						RowSet<Row> resultado = x.result();
						JsonArray a = new JsonArray();
						Integer contador = 0;

						System.out.println("-----TODOS LOS RELÉS-------");

						for (Row r : resultado) {

							if (r.getString("nombre").equals("Relé")) {

								contador += 1;

								a.add(JsonObject.mapFrom(new ActuadorEntity(r.getInteger("valueId"),
										r.getInteger("groupId"), r.getInteger("placaId"), r.getInteger("actuadorId"),
										r.getBoolean("activo"),
										r.getLocalDateTime("fechaHora")
												.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
										r.getString("nombre")

								)));

							}

							else {

								continue;
							}
						}

						System.out.println("");
						System.out.println("Número de ultrasonidos: " + contador);
						rc.response().setStatusCode(200).putHeader("content-type", "application/json")
								.end(a.encodePrettily());

					} else {

						rc.response().setStatusCode(400).putHeader("content-type", "application/json")
								.end((JsonObject.mapFrom(x.cause()).encodePrettily()));
					}

					connection.result().close();

				});

			} else {

				System.out.println(connection.cause().toString());

			}
		});

	}

	private void getAllLDR(RoutingContext rc) {

		SQLClient.getConnection(connection -> {

			if (connection.succeeded()) {

				connection.result().query("SELECT * FROM entregable3.sensores;", x -> {

					if (x.succeeded()) {

						RowSet<Row> resultado = x.result();
						JsonArray a = new JsonArray();
						Integer contador = 0;

						for (Row r : resultado) {

							if (r.getString("nombre").equals("LDR")) {

								contador += 1;

								a.add(JsonObject
										.mapFrom(new SensorEntity(r.getInteger("valueId"), r.getInteger("groupId"),
												r.getInteger("placaId"), r.getInteger("sensorId"), r.getFloat("valor"),
												r.getLocalDateTime("fechaHora")
														.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
												r.getString("nombre")

										)));

							}

							else {

								continue;
							}

						}

						System.out.println("-----TODOS LOS LDR------");
						System.out.println("Número de LDR: " + contador);

						rc.response().setStatusCode(200).putHeader("content-type", "application/json")
						.end(a.encodePrettily());

					} else {

						rc.response().setStatusCode(400).putHeader("content-type", "application/json")
								.end((JsonObject.mapFrom(x.cause()).encodePrettily()));
					}

					connection.result().close();

				});

			} else {

				System.out.println(connection.cause().toString());

			}
		});

	}

	private void getAllUltrasonido(RoutingContext rc) {

		SQLClient.getConnection(connection -> {

			if (connection.succeeded()) {

				connection.result().query("SELECT * FROM entregable3.sensores;", x -> {

					if (x.succeeded()) {

						RowSet<Row> resultado = x.result();
						JsonArray a = new JsonArray();
						Integer contador = 0;

						for (Row r : resultado) {

							if (r.getString("nombre").equals("Ultrasonido")) {

								contador += 1;

								a.add(JsonObject
										.mapFrom(new SensorEntity(r.getInteger("valueId"), r.getInteger("groupId"),
												r.getInteger("placaId"), r.getInteger("sensorId"), r.getFloat("valor"),
												r.getLocalDateTime("fechaHora")
														.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
												r.getString("nombre")

										)));

							}

							else {

								continue;
							}

						}

						System.out.println("------TODOS LOS ULTRASONIDOS-------");
						System.out.println("");
						System.out.println("Número de LDR: " + contador);
						
						rc.response().setStatusCode(200).putHeader("content-type", "application/json")
						.end(a.encodePrettily());

					} else {

						rc.response().setStatusCode(400).putHeader("content-type", "application/json")
								.end((JsonObject.mapFrom(x.cause()).encodePrettily()));
					}

					connection.result().close();

				});

			} else {

				System.out.println(connection.cause().toString());

			}
		});

	}

	private void getLastThreeSensors(RoutingContext rc) {

		SQLClient.getConnection(connection -> {

			if (connection.succeeded()) {

				connection.result().query("SELECT * FROM entregable3.sensores;", x -> {

					if (x.succeeded()) {

						RowSet<Row> resultado = x.result();
						JsonArray a = new JsonArray();
						List<LocalDateTime> fechas = new ArrayList<>();

						for (Row r : resultado) {

							a.add(JsonObject.mapFrom(new SensorEntity(
									r.getInteger("valueId"), 
									r.getInteger("groupId"), 
									r.getInteger("placaId"),
									r.getInteger("sensorId"),
									r.getFloat("valor"), 
									r.getLocalDateTime("fechaHora").format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
									r.getString("nombre")

							)));

							fechas.add(r.getLocalDateTime("fechaHora"));
						}

						List<LocalDateTime> fechasOrdenadas = new ArrayList<>();

						if (fechas.size() >= 3) {

							fechasOrdenadas = fechas.stream().sorted().skip(fechas.size() - 3).toList();
						}

						else {

							// Si no hay suficientes, devolvemos todos

							fechasOrdenadas = fechas.stream().sorted().toList();
						}

						JsonArray threeLastSensors = new JsonArray();

						for (LocalDateTime f : fechasOrdenadas) {

							for (Row r : resultado) {

								if (f.equals(r.getLocalDateTime("fechaHora"))) {

									threeLastSensors.add(JsonObject.mapFrom(new SensorEntity(r.getInteger("valueId"),
											r.getInteger("groupId"), 
											r.getInteger("placaId"),
											r.getInteger("sensorId"),
											r.getFloat("valor"),
											r.getLocalDateTime("fechaHora").format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
											r.getString("nombre"))));

								}

							}

						}

						System.out.println("--------TRES ÚLTIMOS SENSORES-------");
						System.out.println("");
						
						rc.response()
						  .setStatusCode(200)
						  .putHeader("content-type", "application/json")
						  .end(threeLastSensors.encodePrettily());
						

					} else {

						rc.response()
						  .setStatusCode(400)
						  .putHeader("content-type", "application/json")
						   .end((JsonObject.mapFrom(x.cause()).encodePrettily()));
					}

					connection.result().close();

				});

			} else {

				System.out.println(connection.cause().toString());
			}

		});
	}

	private void getLastThreeActuadores(RoutingContext rc) {

		SQLClient.getConnection(connection -> {

			if (connection.succeeded()) {

				connection.result().query("SELECT * FROM entregable3.actuadores;", x -> {

					if (x.succeeded()) {

						RowSet<Row> resultado = x.result();
						JsonArray a = new JsonArray();
						List<LocalDateTime> fechas = new ArrayList<>();

						for (Row r : resultado) {

							a.add(JsonObject.mapFrom(new ActuadorEntity(
									r.getInteger("valueId"), r.getInteger("groupId"), r.getInteger("placaId"),
									r.getInteger("actuadorId"), r.getBoolean("activo"), r.getLocalDateTime("fechaHora")
											.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
									r.getString("nombre")

							)));

							fechas.add(r.getLocalDateTime("fechaHora"));
						}
						
						List<LocalDateTime> fechasOrdenadas = new ArrayList<>();

						if (fechas.size() >= 3) {

							fechasOrdenadas = fechas.stream().sorted().skip(fechas.size() - 3).toList();
						}

						else {

							// Si no hay suficientes, devolvemos todos

							fechasOrdenadas = fechas.stream().sorted().toList();
							//Collections.reverse(fechasOrdenadas);
						}

						JsonArray threeLastActuadores = new JsonArray();

						for (LocalDateTime f : fechasOrdenadas) {

							for (Row r : resultado) {

								if (f.equals(r.getLocalDateTime("fechaHora"))) {

									threeLastActuadores
											.add(JsonObject.mapFrom(new ActuadorEntity(r.getInteger("valueId"),
													r.getInteger("groupId"), r.getInteger("placaId"),
													r.getInteger("actuadorId"), r.getBoolean("activo"),
													r.getLocalDateTime("fechaHora")
															.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
													r.getString("nombre"))));

								}

							}

						}

						System.out.println("--------TRES ÚLTIMOS ACTUADORES-------");
						System.out.println("");

						rc.response()
						  .setStatusCode(200)
						  .putHeader("content-type", "application/json")
						  .end(threeLastActuadores.encodePrettily());

					} else {

						rc.response().setStatusCode(401).putHeader("content-type", "application/json")
								.end((JsonObject.mapFrom(x.cause()).encodePrettily()));
					}

					connection.result().close();

				});

			} else {

				System.out.println(connection.cause().toString());

			}
		});
	}
	
	//MIRAR

	private void lastSensorValue(RoutingContext rc) {

		SQLClient.getConnection(connection -> {

			if (connection.succeeded()) {

				connection.result().query("SELECT * FROM entregable3.actuadores;", x -> {

					if (x.succeeded()) {

						RowSet<Row> resultado = x.result();
						JsonArray a = new JsonArray();
						List<LocalDateTime> fechas = new ArrayList<>();

						for (Row r : resultado) {

							a.add(JsonObject.mapFrom(new SensorEntity(
									r.getInteger("valueId"),
									r.getInteger("groupId"),
									r.getInteger("placaId"),
									r.getInteger("sensorId"),
									r.getFloat("valor"), 
									r.getLocalDateTime("fechaHora").format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
									r.getString("nombre")

							)));

							fechas.add(r.getLocalDateTime("fechaHora"));
						}
						

						List<LocalDateTime> fechasOrdenadas = fechas.stream().sorted().skip(fechas.size() - 1).toList();
						
						JsonArray lastSensor = new JsonArray();

						for (LocalDateTime f : fechasOrdenadas) {

							for (Row r : resultado) {

								if (f.equals(r.getLocalDateTime("fechaHora"))) {

									lastSensor.add(JsonObject.mapFrom(new SensorEntity(r.getInteger("valueId"),
													r.getInteger("groupId"),
													r.getInteger("placaId"),
													r.getInteger("sensorId"),
													r.getFloat("valor"),
													r.getLocalDateTime("fechaHora").format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
													r.getString("nombre"))));

								}

							}

						}

						System.out.println("--------ÚLTIMO SENSOR-------");
						System.out.println("");
						
						rc.response().setStatusCode(200).putHeader("content-type", "application/json")
						.end(lastSensor.encodePrettily());
						
		
					} else {

						rc.response().setStatusCode(401).putHeader("content-type", "application/json")
								.end((JsonObject.mapFrom(x.cause()).encodePrettily()));
						
						

					}

					connection.result().close();

				});

			} else {

				System.out.println(connection.cause().toString());

			}
		});

	}

	private void lastActuadorValue(RoutingContext rc) {

		SQLClient.getConnection(connection -> {

			if (connection.succeeded()) {

				connection.result().query("SELECT * FROM entregable3.actuadores;", x -> {

					if (x.succeeded()) {

						RowSet<Row> resultado = x.result();
						JsonArray a = new JsonArray();
						List<LocalDateTime> fechas = new ArrayList<>();

						for (Row r : resultado) {

							a.add(JsonObject.mapFrom(new ActuadorEntity(
									r.getInteger("valueId"), r.getInteger("groupId"), r.getInteger("placaId"),
									r.getInteger("actuadorId"), r.getBoolean("activo"), r.getLocalDateTime("fechaHora")
											.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
									r.getString("nombre")

							)));

							fechas.add(r.getLocalDateTime("fechaHora"));
						}

						List<LocalDateTime> fechasOrdenadas = fechas.stream().sorted().skip(fechas.size() - 1).toList();
						// fechasOrdenadas.stream().map(s ->
						// s.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")));

						JsonArray lastActuador = new JsonArray();

						for (LocalDateTime f : fechasOrdenadas) {

							for (Row r : resultado) {

								if (f.equals(r.getLocalDateTime("fechaHora"))) {

									lastActuador.add(JsonObject.mapFrom(new ActuadorEntity(r.getInteger("valueId"),
											r.getInteger("groupId"), r.getInteger("placaId"),
											r.getInteger("actuadorId"), r.getBoolean("activo"),
											r.getLocalDateTime("fechaHora")
													.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")),
											r.getString("nombre"))));

								}

							}

						}

						System.out.println("--------ÚLTIMO ACTUADOR-------");
						System.out.println("");

						rc.response().setStatusCode(200).putHeader("content-type", "application/json")
						.end(lastActuador.encodePrettily());

						System.out.println("-----------------------------------");

					} else {

						rc.response().setStatusCode(401).putHeader("content-type", "application/json")
								.end((JsonObject.mapFrom(x.cause()).encodePrettily()));
					}

					connection.result().close();

				});

			} else {

				System.out.println(connection.cause().toString());

			}
		});

	}

	// INSERT

	// Todos estos métodos con preparedQuery ya que son consultas dinámicas

	private void setNewSensor(RoutingContext rc) {

		SQLClient.getConnection(connection -> {

			SensorEntity s = Json.decodeValue(rc.getBodyAsString(), SensorEntity.class);

			Integer groupId = s.getGroupId();
			Integer placaId = s.getPlacaId();
			Integer sensorId = s.getSensorId();
			Float value = s.getValue();
			String fechaHora = s.getTimeStamp();
			String nombre = s.getNombre();

			String nuevaEntrada = "INSERT INTO entregable3.SENSORES( groupId, placaId, sensorId, valor, fechaHora, nombre) VALUES(?,?,?,?,?,?)";
			Tuple nuevoSensor = Tuple.of(groupId, placaId, sensorId, value, fechaHora, nombre);

			if (groupId == null || sensorId == null || placaId == null || value == null || fechaHora == null || nombre == null) {

				rc.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(400).end(
						"Campos requeridos (NOT NULL):\n\tgroupId\n\tplacaId\n\tsensorId\n\tvalor\n\tfechaHora\n\tnombre");
				return;
			}

			else {

				SQLClient.preparedQuery(nuevaEntrada, nuevoSensor, x -> {

					if (x.succeeded()) {

						rc.response().setStatusCode(200).putHeader("content-type", "application/json")
								.end(JsonObject.mapFrom(s).encodePrettily());
					}

					else {

						rc.response().setStatusCode(401).putHeader("content-type", "application/json")
								.end((JsonObject.mapFrom(x.cause()).encodePrettily()));
					}
					
					connection.result().close();

				});

			}

		});
	}

	private void setNewActuador(RoutingContext rc) {
		
	    SQLClient.getConnection(connection -> {

	        ActuadorEntity a = Json.decodeValue(rc.getBodyAsString(), ActuadorEntity.class);

	        Integer groupId = a.getGroupId();
	        Integer actuadorId = a.getActuadorId();
	        Integer placaId = a.getPlacaId();
	        Boolean activo = a.getActivo();
	        String fechaHora = a.getTimeStamp();
	        String nombre = a.getNombreActuador();

	        String nuevaEntrada = "INSERT INTO entregable3.ACTUADORES(groupId, placaId, actuadorId, activo, fechaHora, nombre) VALUES(?,?,?,?,?,?)";
	        Tuple nuevoActuador = Tuple.of(groupId, placaId, actuadorId, activo, fechaHora, nombre);

	        if (groupId == null || actuadorId == null || placaId == null || activo == null || fechaHora == null
	                || nombre == null) {

	            rc.response().putHeader("content-type", "application/json; charset=utf-8").setStatusCode(400).end(
	                    "Campos requeridos (NOT NULL):\n\tgroupId\n\tactuadorId\n\tplacaId\n\tvalor\n\tfechaHora\n\tnombre");
	            return;

	        } else {

	            SQLClient.preparedQuery(nuevaEntrada, nuevoActuador, x -> {

	                if (x.succeeded()) {

	                    rc.response().setStatusCode(200).putHeader("content-type", "application/json")
	                            .end(JsonObject.mapFrom(a).encodePrettily());
	                }

	                else {

	                    rc.response().setStatusCode(401).putHeader("content-type", "application/json")
	                            .end((JsonObject.mapFrom(x.cause()).encodePrettily()));

	                }
	                
	                connection.result().close();

	            });

	        }
	    });
	}


	// ELIMINAR DE LA BBDD

	private void deleteOneSensor(RoutingContext rc) {

		SQLClient.getConnection(connection -> {

			if (connection.succeeded()) {
				
				connection.result().query(
						"DELETE FROM entregable3.sensores where sensorId =" + rc.request().getParam("sensorId"),
						x -> {

							if (x.succeeded()) {
								
								rc.response().setStatusCode(200).putHeader("content-type", "application/json")
										.end("Sensor borrado");

							} else {

								rc.response().setStatusCode(401).putHeader("content-type", "application/json")
										.end((JsonObject.mapFrom(x.cause()).encodePrettily()));
							}

							connection.result().close();

						});

			} else {

				System.out.println(connection.cause().toString());

			}
		});
	}

	private void deleteOneActuador(RoutingContext rc) {

		SQLClient.getConnection(connection -> {

			if (connection.succeeded()) {

				connection.result().query(
						"DELETE FROM entregable3.actuadores where actuadorId =" + rc.request().getParam("actuadorId"),
						x -> {

							if (x.succeeded()) {

								rc.response().setStatusCode(200).putHeader("content-type", "application/json")
										.end("Actuador borrado");

							} else {

								rc.response().setStatusCode(401).putHeader("content-type", "application/json")
										.end((JsonObject.mapFrom(x.cause()).encodePrettily()));
							}

							connection.result().close();

						});

			} else {

				System.out.println(connection.cause().toString());

			}
		});
	}

	@Override
	public void stop(Future<Void> stopFuture) throws Exception {
		super.stop(stopFuture);
	}

}
