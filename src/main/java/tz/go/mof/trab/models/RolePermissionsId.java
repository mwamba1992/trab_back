package tz.go.mof.trab.models;

import java.io.Serializable;
import java.util.Objects;

public class RolePermissionsId implements Serializable {

	private String roleId;

	private String permissionId;

	public RolePermissionsId() {
	}

	public RolePermissionsId(String roleId, String permissionId) {
		this.roleId = roleId;
		this.permissionId = permissionId;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getPermissionId() {
		return permissionId;
	}

	public void setPermissionId(String permissionId) {
		this.permissionId = permissionId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		RolePermissionsId that = (RolePermissionsId) o;
		return roleId.equals(that.roleId) && permissionId.equals(that.permissionId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(roleId, permissionId);
	}

}