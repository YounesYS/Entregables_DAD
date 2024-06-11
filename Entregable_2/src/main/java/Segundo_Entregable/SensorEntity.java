package Segundo_Entregable;

import java.util.Objects;

public class SensorEntity {
	private Integer idGroup;
	private Integer sensorId;
	private Integer placaId;
	private Double values;
	private Long TimeStamp;

	public SensorEntity(Integer idGroup, Integer sensorId, Integer placaId, Double values, Long timeStamp) {
		super();
		this.idGroup = idGroup;
		this.sensorId = sensorId;
		this.placaId = placaId;
		this.values = values;
		TimeStamp = timeStamp;
	}
	public Integer getIdGroup() {
		return idGroup;
	}
	public void setIdGroup(Integer idGroup) {
		this.idGroup = idGroup;
	}
	public Integer getSensorId() {
		return sensorId;
	}
	public void setSensorId(Integer sensorId) {
		this.sensorId = sensorId;
	}
	public Integer getPlacaId() {
		return placaId;
	}
	public void setPlacaId(Integer placaId) {
		this.placaId = placaId;
	}
	public Double getValues() {
		return values;
	}
	public void setValues(Double values) {
		this.values = values;
	}
	public Long getTimeStamp() {
		return TimeStamp;
	}
	public void setTimeStamp(Long timeStamp) {
		TimeStamp = timeStamp;
	}
	@Override
	public int hashCode() {
		return Objects.hash(TimeStamp, idGroup, placaId, sensorId, values);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SensorEntity other = (SensorEntity) obj;
		return Objects.equals(TimeStamp, other.TimeStamp) && Objects.equals(idGroup, other.idGroup)
				&& Objects.equals(placaId, other.placaId) && Objects.equals(sensorId, other.sensorId)
				&& Objects.equals(values, other.values);
	}
	@Override
	public String toString() {
		return "SensorEntity [idGroup=" + idGroup + ", sensorId=" + sensorId + ", placaId=" + placaId + ", values="
				+ values + ", TimeStamp=" + TimeStamp + "]";
	}

}
