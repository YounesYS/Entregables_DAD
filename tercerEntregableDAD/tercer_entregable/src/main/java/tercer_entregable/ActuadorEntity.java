package tercer_entregable;

import java.util.Objects;

import com.google.gson.annotations.SerializedName;

public class ActuadorEntity {
	
	private Integer valueId;
	private Integer groupId;
	private Integer placaId;
	private Integer actuadorId;	
	private Boolean activo;
	@SerializedName("fechaHora")
	private String timeStamp;
	@SerializedName("nombre")
	private String nombreActuador;
	
	//Para los atributos cuyo nombre no coinciden con los nombres de los campos de la BBDD, uso SerializedName

	public ActuadorEntity(Integer groupId, Integer placaId, Integer actuadorId, Boolean activo, String timeStamp, String nombreActuador) {
		super();
		this.groupId = groupId;
		this.actuadorId = actuadorId;
		this.placaId = placaId;
		this.activo = activo;
		this.timeStamp = timeStamp;
		this.nombreActuador = nombreActuador;
	}
	
	//AQUÍ VIENE EL CONSTRUCTOR PARA PODER COGER TODO DE LA BASE DE DATOS
	
	public ActuadorEntity(Integer valueId, Integer groupId, Integer placaId, Integer actuadorId, Boolean activo,
			String timeStamp, String nombreActuador) {
		
		super();
		this.valueId = valueId;
		this.groupId = groupId;
		this.actuadorId = actuadorId;
		this.placaId = placaId;
		this.activo = activo;
		this.timeStamp = timeStamp;
		this.nombreActuador = nombreActuador;
	}
	
	
	public ActuadorEntity() {
		super();
		// TODO Auto-generated constructor stub
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
	public Integer getActuadorId() {
		return actuadorId;
	}
	public void setActuadorId(Integer actuadorId) {
		this.actuadorId = actuadorId;
	}
	public Boolean getActivo() {
		return activo;
	}
	public void setActivo(Boolean activo) {
		this.activo = activo;
	}
	
	public void setTimeStamp(String TimeStamp) {
		this.timeStamp = TimeStamp;
	}
	
	public String getTimeStamp() {
		return timeStamp;
	}
	
	public String getNombreActuador() {
		
		return nombreActuador;
		
	}
	
	public void setNombreActuador(String nombreActuador) {
		
		this.nombreActuador = nombreActuador;
	}

	@Override
	public int hashCode() {
		return Objects.hash(activo, actuadorId, nombreActuador, placaId, timeStamp);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ActuadorEntity other = (ActuadorEntity) obj;
		return Objects.equals(activo, other.activo) && Objects.equals(actuadorId, other.actuadorId)
				&& Objects.equals(nombreActuador, other.nombreActuador) && Objects.equals(placaId, other.placaId)
				&& Objects.equals(timeStamp, other.timeStamp);
	}

	@Override
	public String toString() {
		return "ActuadorEntity [actuadorId=" + actuadorId + ", placaId=" + placaId + ", activo=" + activo
				+ ", timeStamp=" + timeStamp + ", nombreActuador=" + nombreActuador + "]";
	}
	
}
