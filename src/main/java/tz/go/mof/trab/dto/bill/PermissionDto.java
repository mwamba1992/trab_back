package tz.go.mof.trab.dto.bill;

import java.time.LocalDateTime;

import javax.persistence.Basic;
import javax.persistence.Column;
import org.hibernate.annotations.CreationTimestamp;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PermissionDto {
 
	private String name;

	private String displayName;

	private String serviceName;

	private Boolean active;
	
	private String permissionCategoryId;

	@Basic(optional = false)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@CreationTimestamp
	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt = LocalDateTime.now();

	@Basic(optional = true)
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	@Column(name = "updated_at")
	private LocalDateTime updatedAt = LocalDateTime.now();
	
}
