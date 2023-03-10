package tz.go.mof.trab.models;

import java.io.Serializable;
import java.util.Objects;

public class UserRoleId implements Serializable {

	private String userId;

	private String roleId;

	public UserRoleId() {
	}

	public UserRoleId(String userId, String roleId) {
		this.roleId = roleId;
		this.userId = userId;
	}

	public String getRoleId() {
		return roleId;
	}

	public void setRoleId(String roleId) {
		this.roleId = roleId;
	}

	public String getUserId() {

		return userId;
	}

	public void setUserId(String userId) {

		this.userId = userId;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		UserRoleId that = (UserRoleId) o;
		return roleId.equals(that.roleId) && userId.equals(that.userId);
	}

	@Override
	public int hashCode() {
		return Objects.hash(roleId, userId);
	}

}