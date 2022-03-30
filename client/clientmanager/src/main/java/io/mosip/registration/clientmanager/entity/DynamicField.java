package io.mosip.registration.clientmanager.entity;


import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * This Entity Class contains list of dynamic fields
 * which will be displayed in UI with respect to language code.
 * The data for this table will come through sync from server master table
 *
 * @author Anshul vanawat
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Entity(tableName = "dynamic_field")
public class DynamicField {
	
	@PrimaryKey
	@ColumnInfo(name="id")
	private String id;

	@ColumnInfo(name="name")
	private String name;

	@ColumnInfo(name="description")
	private String description;
	
	@ColumnInfo(name="lang_code")
	private String langCode;
	
	@ColumnInfo(name="data_type")
	private String dataType;
	
	@ColumnInfo(name="value_json")
	private String valueJson;
	
	@ColumnInfo(name="is_active")
	private boolean isActive;
}

