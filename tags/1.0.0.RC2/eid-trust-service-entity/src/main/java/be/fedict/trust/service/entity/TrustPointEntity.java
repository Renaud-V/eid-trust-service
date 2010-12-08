/*
 * eID Trust Service Project.
 * Copyright (C) 2009-2010 FedICT.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License version
 * 3.0 as published by the Free Software Foundation.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, see 
 * http://www.gnu.org/licenses/.
 */

package be.fedict.trust.service.entity;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import javax.ejb.TimerHandle;
import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "ts_trust_point")
@NamedQueries({@NamedQuery(name = TrustPointEntity.QUERY_ALL, query = "FROM TrustPointEntity")})
public class TrustPointEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String QUERY_ALL = "ts_tp.q.all";

    private String name;

    private long crlRefreshInterval;
    private Date fireDate;

    private CertificateAuthorityEntity certificateAuthority;

    /**
     * Default constructor.
     */
    public TrustPointEntity() {
        super();
    }

    /**
     * Main constructor.
     *
     * @param crlRefreshInterval
     * @param certificateAuthority
     */
    public TrustPointEntity(long crlRefreshInterval,
                            CertificateAuthorityEntity certificateAuthority) {
        this.name = certificateAuthority.getName();
        this.certificateAuthority = certificateAuthority;
        this.crlRefreshInterval = crlRefreshInterval;
    }

    @Id
    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @OneToOne(optional = false, cascade = CascadeType.REMOVE)
    public CertificateAuthorityEntity getCertificateAuthority() {

        return this.certificateAuthority;
    }

    public void setCertificateAuthority(
            CertificateAuthorityEntity certificateAuthority) {

        this.certificateAuthority = certificateAuthority;
    }

    public long getCrlRefreshInterval() {

        return this.crlRefreshInterval;
    }

    public void setCrlRefreshInterval(long crlRefreshInterval) {

        this.crlRefreshInterval = crlRefreshInterval;
    }

    public Date getFireDate() {

        return this.fireDate;
    }

    public void setFireDate(Date fireDate) {

        this.fireDate = fireDate;
    }

    @Override
    public boolean equals(Object obj) {

        if (this == obj) {
            return true;
        }
        if (null == obj) {
            return false;
        }
        if (!(obj instanceof TrustPointEntity)) {
            return false;
        }
        TrustPointEntity rhs = (TrustPointEntity) obj;
        return new EqualsBuilder().append(this.name, rhs.name).isEquals();

    }

    @Override
    public int hashCode() {

        return new HashCodeBuilder().append(this.name).toHashCode();
    }

    @Override
    public String toString() {

        return new ToStringBuilder(this).append("name", this.name).append(
                "crlRefresh", this.crlRefreshInterval).toString();
	}

}