/*
 * Copyright 2011-2015 PrimeFaces Extensions
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * $Id$
 */

package org.primefaces.extensions.component.ajaxerrorhandler;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;

import org.apache.commons.lang3.StringUtils;
import org.primefaces.renderkit.CoreRenderer;

/**
 * Renderer for the {@link AjaxErrorHandler} component.
 *
 * @author Pavol Slany / last modified by $Author$
 * @version $Revision$
 * @since 0.5
 */
public class AjaxErrorHandlerRenderer extends CoreRenderer {

	private static final String HOSTNAME_ALREADY_DEFINED_KEY =
			AjaxErrorHandlerRenderer.class.getCanonicalName() + ".HOSTNAME_ALREADY_DEFINED";

	@Override
	public void encodeEnd(final FacesContext context, final UIComponent component) throws IOException {
		encodeScript(context, (AjaxErrorHandler) component);
	}

	protected void encodeScript(final FacesContext context, final AjaxErrorHandler ajaxErrorHandler) throws IOException {
		ResponseWriter writer = context.getResponseWriter();
		String clientId = ajaxErrorHandler.getClientId(context);
		String widgetVar = ajaxErrorHandler.resolveWidgetVar();

		startScript(writer, clientId);

		if (widgetVar != null) {
			writer.write("$(function(){");
			writer.write(widgetVar);
			writer.write(" = PrimeFacesExt.getAjaxErrorHandlerInstance();});");
		}

		if (context.getExternalContext().getRequestMap().get(HOSTNAME_ALREADY_DEFINED_KEY) == null) {
			writer.write("$(function(){PrimeFacesExt.getAjaxErrorHandlerInstance().setHostname('");
			writer.write(getHostname());
			writer.write("');});");
			context.getExternalContext().getRequestMap().put(HOSTNAME_ALREADY_DEFINED_KEY, true);
		}

		writer.write("$(function(){PrimeFacesExt.getAjaxErrorHandlerInstance().addErrorSettings({");

		boolean optionWritten = false;
		
		optionWritten |= writeStringOption(writer, AjaxErrorHandler.PropertyKeys.type.toString(), ajaxErrorHandler.getType(), optionWritten);
		optionWritten |= writeStringOption(writer, AjaxErrorHandler.PropertyKeys.title.toString(), ajaxErrorHandler.getTitle(), optionWritten);
		optionWritten |= writeStringOption(writer, AjaxErrorHandler.PropertyKeys.body.toString(), ajaxErrorHandler.getBody(), optionWritten);
		optionWritten |= writeStringOption(writer, AjaxErrorHandler.PropertyKeys.button.toString(), ajaxErrorHandler.getButton(), optionWritten);

		if (!StringUtils.isEmpty(ajaxErrorHandler.getButtonOnclick())) {
			optionWritten |= writeFunctionOption(writer, AjaxErrorHandler.PropertyKeys.buttonOnclick.toString(),
					"function(){" + ajaxErrorHandler.getButtonOnclick() + "}", optionWritten);
		}

		if (!StringUtils.isEmpty(ajaxErrorHandler.getOnerror())) {
			optionWritten |= writeFunctionOption(writer, AjaxErrorHandler.PropertyKeys.onerror.toString(),
					"function(error, response){" + ajaxErrorHandler.getOnerror() + "}", optionWritten);
		}

		writer.write("});});");

		endScript(writer);
	}

	protected boolean writeStringOption(final ResponseWriter writer, final String type, final String value, final boolean optionWritten)
			throws IOException {

		boolean writeOption = !StringUtils.isEmpty(value);

		if (writeOption) {
			if (optionWritten) {
				writer.write(",");
			}

			writer.write("'");
			writer.write(type);
			writer.write("':'");
			writer.write(value);
			writer.write("'");
		}

		return writeOption;
	}

	protected boolean writeFunctionOption(final ResponseWriter writer, final String type, final String value, final boolean optionWritten)
			throws IOException {

		boolean writeOption = !StringUtils.isEmpty(value);
		
		if (writeOption) {
			if (optionWritten) {
				writer.write(",");
			}

			writer.write("'");
			writer.write(type);
			writer.write("':");
			writer.write(value);
		}

		return writeOption;
	}

	protected String getHostname() throws UnknownHostException {
		try {
			return InetAddress.getLocalHost().getHostName();
		} catch (UnknownHostException e) {
			return "???unknown???";
		}
	}

	@Override
	public void encodeChildren(final FacesContext context, final UIComponent component) throws IOException {
		//do nothing
	}

	@Override
	public boolean getRendersChildren() {
		return true;
	}
}
