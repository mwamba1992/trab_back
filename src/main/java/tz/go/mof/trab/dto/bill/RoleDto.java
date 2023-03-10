package tz.go.mof.trab.dto.bill;

import java.time.LocalDateTime;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoleDto {
 
	@NotNull (message="Name can't be null")
	@NotBlank(message="Role name is mandatory")
	private String name;
	
	@NotNull(message="Description can't be null")
	@NotBlank(message="Description is mandatory")
	private String description;
	
	private String createdBy;  
	
	private String updatedBy;
	
	private LocalDateTime createdAt;
	
	private LocalDateTime updatedAt;

}
