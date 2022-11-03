/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2006-2014 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2014 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.horizon.alarms.db.impl.dto;

import java.io.Serializable;
import java.util.Objects;
import java.util.StringJoiner;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="meta-data")
@Embeddable
public class MetaDataDTO implements Serializable {

    private static final long serialVersionUID = 3529745790145204662L;

    private String context;
    private String key;
    private String value;

    public MetaDataDTO() {
    }

    public MetaDataDTO(String context, String key, String value) {
        this.context = Objects.requireNonNull(context);
        this.key = Objects.requireNonNull(key);
        this.value = Objects.requireNonNull(value);
    }

    @XmlElement(name="context")
    @Column(name="context", nullable = false)
    public String getContext(){
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    @XmlElement(name="key")
    @Column(name="key", nullable = false)
    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    
    @XmlElement(name="value")
    @Column(name="value", nullable = false)
    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", MetaDataDTO.class.getSimpleName() + "[", "]")
                .add("context='" + context + "'")
                .add("key='" + key + "'")
                .add("value='" + value + "'")
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MetaDataDTO that = (MetaDataDTO) o;
        return com.google.common.base.Objects.equal(context, that.context) &&
                com.google.common.base.Objects.equal(key, that.key) &&
                com.google.common.base.Objects.equal(value, that.value);
    }

    @Override
    public int hashCode() {
        return com.google.common.base.Objects.hashCode(context, key, value);
    }
}
