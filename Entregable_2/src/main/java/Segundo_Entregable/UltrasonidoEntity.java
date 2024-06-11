package Segundo_Entregable;

import java.util.Objects;


public class UltrasonidoEntity {
	Integer placaId;
	Integer sensorId;
	Integer distancia;
	
	public UltrasonidoEntity(Integer placaId, Integer sensorId, Integer distancia) {
		super();
		this.placaId = placaId;
		this.sensorId = sensorId;
		this.distancia = distancia;
	}

	public Integer getPlacaId() {
		return placaId;
	}

	public Integer getSensorId() {
		return sensorId;
	}

	public Integer getDistancia() {
		return distancia;
	}

	

	public void setPlacaId(Integer placaId) {
		this.placaId = placaId;
	}

	public void setSensorId(Integer sensorId) {
		this.sensorId = sensorId;
	}

	public void setDistancia(Integer distancia) {
		this.distancia = distancia;
	}

	@Override
	public int hashCode() {
		return Objects.hash(distancia, placaId, sensorId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UltrasonidoEntity other = (UltrasonidoEntity) obj;
		return Objects.equals(distancia, other.distancia) && Objects.equals(placaId, other.placaId)
				&& Objects.equals(sensorId, other.sensorId);
	}

	@Override
	public String toString() {
		return "UltraSonido [placaId=" + placaId + ", sensorId=" + sensorId + ", distancia=" + distancia + "]";
	}	
}
