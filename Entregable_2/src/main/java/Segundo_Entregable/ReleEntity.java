package Segundo_Entregable;

import java.util.Objects;


public class ReleEntity {
	Integer placaId;
	Integer sensorId;
	Long timeStamp;
	Boolean activo;
	
	public ReleEntity(Integer placaId, Integer sensorId, Long timeStamp, Boolean activo) {
		super();
		this.placaId = placaId;
		this.sensorId = sensorId;
		this.timeStamp = timeStamp;
		this.activo = activo;
	}

	public Integer getPlacaId() {
		return placaId;
	}

	public Integer getSensorId() {
		return sensorId;
	}

	public Long getTimeStamp() {
		return timeStamp;
	}

	public Boolean getActivo() {
		return activo;
	}

	

	@Override
	public int hashCode() {
		return Objects.hash(activo, placaId, sensorId, timeStamp);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ReleEntity other = (ReleEntity) obj;
		return Objects.equals(activo, other.activo) && Objects.equals(placaId, other.placaId)
				&& Objects.equals(sensorId, other.sensorId) && Objects.equals(timeStamp, other.timeStamp);
	}

	@Override
	public String toString() {
		return "Rele [placaId=" + placaId + ", sensorId=" + sensorId + ", timeStamp=" + timeStamp + ", activo="
				+ activo + "]";
	}
	
	
}

