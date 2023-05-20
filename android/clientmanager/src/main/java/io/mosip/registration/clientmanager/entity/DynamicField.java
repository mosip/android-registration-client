package io.mosip.registration.clientmanager.entity;


import static androidx.room.ColumnInfo.TEXT;

import androidx.annotation.NonNull;
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
@Entity(tableName = "dynamic_field")
public class DynamicField {

	@NonNull
	@PrimaryKey
	@ColumnInfo(name="id")
	private String id;

	@ColumnInfo(name="name")
	private String name;

	@ColumnInfo(name="lang_code")
	private String langCode;
	
	@ColumnInfo(name="data_type")
	private String dataType;
	
	@ColumnInfo(name="value_json", typeAffinity = TEXT)
	private String valueJson;
	
	@ColumnInfo(name="is_active")
	private Boolean isActive;

	@ColumnInfo(name="is_deleted")
	private Boolean isDeleted;
}

