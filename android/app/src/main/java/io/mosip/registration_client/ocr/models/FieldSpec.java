/*
 * Copyright (c) Modular Open Source Identity Platform
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package io.mosip.registration_client.ocr.models;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class FieldSpec {

    private final String id;
    private final String type;
    private final String controlType;
    private final String subType;

    public FieldSpec(@NonNull String id, @Nullable String type,
                      @Nullable String controlType, @Nullable String subType) {
        this.id = id;
        this.type = type;
        this.controlType = controlType;
        this.subType = subType;
    }

    @NonNull
    public String getId() { return id; }

    @Nullable
    public String getType() { return type; }

    @Nullable
    public String getControlType() { return controlType; }

    @Nullable
    public String getSubType() { return subType; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof FieldSpec)) return false;
        return id.equals(((FieldSpec) o).id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}