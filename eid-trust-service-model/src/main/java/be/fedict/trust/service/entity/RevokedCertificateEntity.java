/*
 * eID Trust Service Project.
 * Copyright (C) 2009 FedICT.
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

import java.io.Serializable;
import java.math.BigInteger;
import java.util.Date;

import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

@Entity
@Table(name = "revoked_certificates")
@NamedQueries( { @NamedQuery(name = RevokedCertificateEntity.QUERY_WHERE_ISSUER_SERIAL, query = "SELECT rc FROM RevokedCertificateEntity "
		+ "AS rc WHERE rc.pk.issuer = :issuer AND rc.pk.serialNumber = :serialNumber ORDER BY rc.crlNumber DESC") })
public class RevokedCertificateEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	public static final String QUERY_WHERE_ISSUER_SERIAL = "rc.q.i.s";

	private RevokedCertificatePK pk;

	private Date revocationDate;

	private BigInteger crlNumber;

	public RevokedCertificateEntity() {
		super();
	}

	public RevokedCertificateEntity(String issuerName, BigInteger serialNumber,
			Date revocationDate, BigInteger crlNumber) {
		this.pk = new RevokedCertificatePK(issuerName, serialNumber, crlNumber);
		this.revocationDate = revocationDate;
		this.crlNumber = crlNumber;
	}

	@EmbeddedId
	public RevokedCertificatePK getPk() {
		return this.pk;
	}

	public void setPk(RevokedCertificatePK pk) {
		this.pk = pk;
	}

	@Temporal(TemporalType.TIMESTAMP)
	public Date getRevocationDate() {
		return this.revocationDate;
	}

	public void setRevocationDate(Date revocationDate) {
		this.revocationDate = revocationDate;
	}

	public BigInteger getCrlNumber() {
		return this.crlNumber;
	}

	public void setCrlNumber(BigInteger crlNumber) {
		this.crlNumber = crlNumber;
	}

	@Override
	public boolean equals(Object obj) {

		if (this == obj) {
			return true;
		}
		if (null == obj) {
			return false;
		}
		if (false == obj instanceof RevokedCertificateEntity) {
			return false;
		}
		RevokedCertificateEntity rhs = (RevokedCertificateEntity) obj;
		return new EqualsBuilder().append(this.pk, rhs.pk).isEquals();
	}

	@Override
	public int hashCode() {

		return new HashCodeBuilder().append(pk).toHashCode();
	}

	@Override
	public String toString() {

		return new ToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE)
				.append("pk", this.pk).append("revocationDate",
						this.revocationDate)
				.append("crlNumber", this.crlNumber).toString();
	}

}