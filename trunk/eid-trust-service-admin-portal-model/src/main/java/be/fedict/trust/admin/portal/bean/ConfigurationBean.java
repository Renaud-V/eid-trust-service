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

package be.fedict.trust.admin.portal.bean;

import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Remove;
import javax.ejb.Stateful;
import javax.faces.model.SelectItem;

import org.jboss.ejb3.annotation.LocalBinding;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.Destroy;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.log.Log;

import be.fedict.trust.admin.portal.AdminConstants;
import be.fedict.trust.admin.portal.Configuration;
import be.fedict.trust.service.ConfigurationService;
import be.fedict.trust.service.TrustServiceConstants;
import be.fedict.trust.service.entity.ClockDriftConfigEntity;
import be.fedict.trust.service.entity.NetworkConfigEntity;
import be.fedict.trust.service.entity.TimeProtocol;
import be.fedict.trust.service.exception.InvalidCronExpressionException;

@Stateful
@Name(AdminConstants.ADMIN_SEAM_PREFIX + "config")
@LocalBinding(jndiBinding = AdminConstants.ADMIN_JNDI_CONTEXT
		+ "ConfigurationBean")
public class ConfigurationBean implements Configuration {

	@Logger
	private Log log;

	@EJB
	private ConfigurationService configurationService;

	@In
	FacesMessages facesMessages;

	private String proxyHost;
	private int proxyPort;
	private boolean enabled;

	private String clockDriftProtocol;
	private String clockDriftServer;
	private int clockDriftTimeout;
	private int clockDriftMaxClockOffset;
	private String clockDriftCron;

	@In(value = "language", required = false)
	@Out(value = "language", required = false, scope = ScopeType.CONVERSATION)
	private String language;

	private String informationMessage;

	/**
	 * {@inheritDoc}
	 */
	@Remove
	@Destroy
	public void destroyCallback() {

		this.log.debug("#destroy");
	}

	/**
	 * {@inheritDoc}
	 */
	@PostConstruct
	public void initialize() {

		this.log.debug("#initialize");
		NetworkConfigEntity networkConfig = this.configurationService
				.getNetworkConfig();
		this.proxyHost = networkConfig.getProxyHost();
		this.proxyPort = networkConfig.getProxyPort();
		this.enabled = networkConfig.isEnabled();

		ClockDriftConfigEntity clockDriftConfig = this.configurationService
				.getClockDriftDetectionConfig();
		this.clockDriftProtocol = clockDriftConfig.getTimeProtocol().name();
		this.clockDriftServer = clockDriftConfig.getServer();
		this.clockDriftTimeout = clockDriftConfig.getTimeout();
		this.clockDriftMaxClockOffset = clockDriftConfig.getMaxClockOffset();
		this.clockDriftCron = clockDriftConfig.getCron();
	}

	/**
	 * {@inheritDoc}
	 */
	public String saveNetworkConfig() {

		this.log.debug(
				"save network config: proxyHost=#0  proxyPort=#1  enabled=#2",
				this.proxyHost, this.proxyPort, this.enabled);

		this.configurationService.saveNetworkConfig(proxyHost, proxyPort,
				enabled);
		return "success";
	}

	/**
	 * {@inheritDoc}
	 */
	public String saveClockDriftConfig() {

		this.log
				.debug(
						"save clock drift config: protocol=#0 server=#1 timeout=#2 maxClockOffset=#3 cron=#4",
						this.clockDriftProtocol, this.clockDriftServer,
						this.clockDriftTimeout, this.clockDriftMaxClockOffset,
						this.clockDriftCron);

		try {
			this.configurationService.saveClockDriftConfig(TimeProtocol
					.valueOf(this.clockDriftProtocol), this.clockDriftServer,
					this.clockDriftTimeout, this.clockDriftMaxClockOffset,
					this.clockDriftCron);
		} catch (InvalidCronExpressionException e) {
			this.facesMessages.addToControlFromResourceBundle("cron",
					StatusMessage.Severity.ERROR, "errorCronExpressionInvalid");
			return null;
		}

		return "success";
	}

	/**
	 * {@inheritDoc}
	 */
	@Begin(join = true)
	public String editInfoMessage() {

		this.log.debug("edit info message for language=#0", this.language);
		this.informationMessage = this.configurationService.findText(
				TrustServiceConstants.INFO_MESSAGE_KEY, new Locale(
						this.language));
		return "edit";
	}

	/**
	 * {@inheritDoc}
	 */
	@End
	public String saveInfoMessage() {

		this.log.debug("save info message for language=#0", this.language);
		this.configurationService.saveText(
				TrustServiceConstants.INFO_MESSAGE_KEY, new Locale(
						this.language), this.informationMessage);
		return "success";
	}

	/**
	 * {@inheritDoc}
	 */
	public String getProxyHost() {

		return this.proxyHost;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getProxyPort() {

		return this.proxyPort;
	}

	/**
	 * {@inheritDoc}
	 */
	public boolean isEnabled() {

		return this.enabled;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setEnabled(boolean enabled) {

		this.enabled = enabled;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setProxyHost(String proxyHost) {

		this.proxyHost = proxyHost;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setProxyPort(int proxyPort) {

		this.proxyPort = proxyPort;
	}

	/**
	 * {@inheritDoc}
	 */
	@Factory("clockDriftProtocols")
	public List<SelectItem> clockDriftProtocolFactory() {

		List<SelectItem> protocols = new LinkedList<SelectItem>();
		for (TimeProtocol protocol : TimeProtocol.values()) {
			protocols.add(new SelectItem(protocol.name(), protocol.name()));
		}
		return protocols;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getClockDriftCron() {

		return this.clockDriftCron;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getClockDriftMaxClockOffset() {

		return this.clockDriftMaxClockOffset;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getClockDriftProtocol() {

		return this.clockDriftProtocol;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getClockDriftServer() {

		return this.clockDriftServer;
	}

	/**
	 * {@inheritDoc}
	 */
	public int getClockDriftTimeout() {

		return this.clockDriftTimeout;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setClockDriftCron(String clockDriftCron) {

		this.clockDriftCron = clockDriftCron;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setClockDriftMaxClockOffset(int clockDriftMaxClockOffset) {

		this.clockDriftMaxClockOffset = clockDriftMaxClockOffset;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setClockDriftProtocol(String clockDriftProtocol) {

		this.clockDriftProtocol = clockDriftProtocol;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setClockDriftServer(String clockDriftServer) {

		this.clockDriftServer = clockDriftServer;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setClockDriftTimeout(int clockDriftTimeout) {

		this.clockDriftTimeout = clockDriftTimeout;
	}

	/**
	 * {@inheritDoc}
	 */
	@Factory("supportedLanguages")
	public List<SelectItem> supportedLanguagesFactory() {

		List<SelectItem> locales = new LinkedList<SelectItem>();
		for (String language : this.configurationService
				.listLanguages(TrustServiceConstants.INFO_MESSAGE_KEY)) {
			locales.add(new SelectItem(language, language));
		}
		return locales;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getLanguage() {

		return this.language;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setLanguage(String language) {

		this.language = language;
	}

	/**
	 * {@inheritDoc}
	 */
	public String getInformationMessage() {

		return this.informationMessage;
	}

	/**
	 * {@inheritDoc}
	 */
	public void setInformationMessage(String informationMessage) {

		this.informationMessage = informationMessage;
	}
}