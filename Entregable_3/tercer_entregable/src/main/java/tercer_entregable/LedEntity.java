package tercer_entregable;

import java.util.Objects;



public class LedEntity {
	Integer placaId;
	Integer sensorId;
	Double values;
	Long TimeStamps;
	
	public LedEntity(Integer placaId, Integer sensorId, Double ohmios, Long timeStamps) {
		super();
		this.placaId = placaId;
		this.sensorId = sensorId;
		this.values = ohmios;
		TimeStamps = timeStamps;
	}

	public Integer getPlacaId() {
		return placaId;
	}

	public Integer getSensorId() {
		return sensorId;
	}

	public Double getOhmios() {
		return values;
	}

	public Long getTimeStamps() {
		return TimeStamps;
	}

	
	public void setPlacaId(Integer placaId) {
		this.placaId = placaId;
	}

	public void setSensorId(Integer sensorId) {
		this.sensorId = sensorId;
	}

	public void setOhmios(Double ohmios) {
		this.values = ohmios;
	}

	public void setTimeStamps(Long timeStamps) {
		TimeStamps = timeStamps;
	}

	@Override
	public int hashCode() {
		return Objects.hash(TimeStamps, placaId, sensorId, values);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LedEntity other = (LedEntity) obj;
		return Objects.equals(TimeStamps, other.TimeStamps) && Objects.equals(placaId, other.placaId)
				&& Objects.equals(sensorId, other.sensorId) && Objects.equals(values, other.values);
	}

	
}


