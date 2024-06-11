package Segundo_Entregable;

import java.util.Objects;

public class ActuadorEntity {
	Integer idGroup;
	Integer actuadorId;
	Integer placaId;
	Boolean activo;
	Long TimeStamp;
	public Integer getIdGroup() {
		return idGroup;
	}
	public void setIdGroup(Integer idGroup) {
		this.idGroup = idGroup;
	}
	public Integer getActuadorId() {
		return actuadorId;
	}
	public void setActuadorId(Integer actuadorId) {
		this.actuadorId = actuadorId;
	}
	public Integer getPlacaId() {
		return placaId;
	}
	public void setPlacaId(Integer placaId) {
		this.placaId = placaId;
	}
	public Boolean getActivo() {
		return activo;
	}
	public void setActivo(Boolean activo) {
		this.activo = activo;
	}
	public Long getTimeStamp() {
		return TimeStamp;
	}
	public void setTimeStamp(Long timeStamp) {
		TimeStamp = timeStamp;
	}
	@Override
	public int hashCode() {
		return Objects.hash(TimeStamp, activo, actuadorId, idGroup, placaId);
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
		return Objects.equals(TimeStamp, other.TimeStamp) && Objects.equals(activo, other.activo)
				&& Objects.equals(actuadorId, other.actuadorId) && Objects.equals(idGroup, other.idGroup)
				&& Objects.equals(placaId, other.placaId);
	}
	public ActuadorEntity(Integer idGroup, Integer actuadorId, Integer placaId, Boolean activo, Long timeStamp) {
		super();
		this.idGroup = idGroup;
		this.actuadorId = actuadorId;
		this.placaId = placaId;
		this.activo = activo;
		TimeStamp = timeStamp;
	}
	@Override
	public String toString() {
		return "ActuadorEntity [idGroup=" + idGroup + ", actuadorId=" + actuadorId + ", placaId=" + placaId
				+ ", activo=" + activo + ", TimeStamp=" + TimeStamp + "]";
	}

}


	

