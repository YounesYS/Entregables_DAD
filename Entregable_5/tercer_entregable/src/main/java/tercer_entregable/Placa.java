package tercer_entregable;

import java.util.Objects;

public class Placa {
	
	private Integer valueId;
	private Integer groupId;
	private Integer placaId;
	
	
	public Placa(Integer valueId, Integer groupId, Integer placaId) {
		super();
		this.valueId = valueId;
		this.groupId = groupId;
		this.placaId = placaId;
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


	@Override
	public int hashCode() {
		return Objects.hash(groupId, placaId, valueId);
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Placa other = (Placa) obj;
		return Objects.equals(groupId, other.groupId) && Objects.equals(placaId, other.placaId)
				&& Objects.equals(valueId, other.valueId);
	}


	@Override
	public String toString() {
		return "Placa [valueId=" + valueId + ", groupId=" + groupId + ", placaId=" + placaId + "]";
	}
	
	
	
	
	
}
