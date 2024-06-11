package tercer_entregable;

import java.util.Objects;

import com.google.gson.annotations.SerializedName;

public class SensorEntity {
	
	private Integer valueId;
	private Integer groupId;
	private Integer placaId;
	private Integer sensorId;
	//@SerializedName("valor")
	private Float value;
	//@SerializedName("fechaHora")
	private String timeStamp;
	@SerializedName("nombre")
	private String nombreSensor;
	
	
	
	public SensorEntity(Integer placaId, Integer sensorId, Float value, String timeStamp, String nombreSensor) {
		super();
		this.placaId = placaId;
		this.sensorId = sensorId;
		this.value = value;
		this.timeStamp = timeStamp;
		this.nombreSensor = nombreSensor;
	} 
	
	//CONSTRUCTOR PARA BBDD
	
	public SensorEntity(Integer valueId, Integer groupId, Integer placaId, Integer sensorId, Float value,
			String timeStamp, String nombreSensor) {
		
		super();
		this.valueId = valueId;
		this.groupId = groupId;
		this.placaId = placaId;
		this.sensorId = sensorId;
		this.value = value;
		this.timeStamp = timeStamp;
		this.nombreSensor = nombreSensor;
	}
	
	
	public SensorEntity() {
		super();
	}

	public Integer getValueId() {
		return valueId;
	}
	
	public void setValueId(Integer valueId) {
		this.valueId = valueId;
	}
	
	public Integer getGroupId() {
		return groupId;
	}
	public void setGroupId(Integer groupId) {
		this.groupId = groupId;	
	}
	
	public Integer getPlacaId() {
		return placaId;
	}
	public void setPlacaId(Integer placaId) {
		this.placaId = placaId;
	}
	public Integer getSensorId() {
		return sensorId;
	}
	public void setSensorId(Integer sensorId) {
		this.sensorId = sensorId;
	}
	public Float getValue() {
		return value;
	}
	public void setValue(Float values) {
		this.value = values;
	}
	
	public String getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(String timeStamp) {
		this.timeStamp = timeStamp;
	} 
	
	public String getNombre() {
		
		return nombreSensor;
	}
	
	public void setNombre(String nombre) {
		
		this.nombreSensor = nombre;
		
	}
	

	@Override
	public int hashCode() {
		return Objects.hash(timeStamp, nombreSensor, placaId, sensorId, value);
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
		return Objects.equals(timeStamp, other.timeStamp) && Objects.equals(nombreSensor, other.nombreSensor)
				&& Objects.equals(placaId, other.placaId) && Objects.equals(sensorId, other.sensorId)
				&& Objects.equals(value, other.value);
	}

	@Override
	public String toString() {
		return "SensorEntity [sensorId=" + sensorId + ", placaId=" + placaId + ", values=" + value + ", TimeStamp="
				+ timeStamp + ", nombre=" + nombreSensor + "]";
	}

	
}
