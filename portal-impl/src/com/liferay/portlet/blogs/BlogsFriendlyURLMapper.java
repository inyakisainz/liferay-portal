/**
 * Copyright (c) 2000-2009 Liferay, Inc. All rights reserved.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.liferay.portlet.blogs;

import com.liferay.portal.kernel.portlet.BaseFriendlyURLMapper;
import com.liferay.portal.kernel.portlet.LiferayPortletURL;
import com.liferay.portal.kernel.portlet.LiferayWindowState;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.StringPool;
import com.liferay.portal.kernel.util.Validator;
import com.liferay.portal.util.PortletKeys;

import java.util.Map;

import javax.portlet.PortletMode;
import javax.portlet.WindowState;

/**
 * <a href="BlogsFriendlyURLMapper.java.html"><b><i>View Source</i></b></a>
 *
 * @author Brian Wing Shun Chan
 *
 */
public class BlogsFriendlyURLMapper extends BaseFriendlyURLMapper {

	public String buildPath(LiferayPortletURL portletURL) {
		String friendlyURLPath = null;

		String strutsAction = GetterUtil.getString(
			portletURL.getParameter("struts_action"));

		if (strutsAction.equals("/blogs/rss")) {
			friendlyURLPath = "/blogs/rss";
		}
		else if (strutsAction.equals("/blogs/view_entry")) {
			String entryId = portletURL.getParameter("entryId");

			String urlTitle = portletURL.getParameter("urlTitle");

			if (Validator.isNotNull(entryId)) {
				friendlyURLPath = "/blogs/" + entryId;

				portletURL.addParameterIncludedInPath("entryId");
			}
			else if (Validator.isNotNull(urlTitle)) {
				friendlyURLPath = "/blogs/" + urlTitle;

				portletURL.addParameterIncludedInPath("urlTitle");
			}
		}

		if (Validator.isNotNull(friendlyURLPath)) {
			portletURL.addParameterIncludedInPath("p_p_id");

			portletURL.addParameterIncludedInPath("struts_action");

			WindowState windowState = portletURL.getWindowState();

			if (windowState.equals(WindowState.MAXIMIZED)) {
				friendlyURLPath += StringPool.SLASH + windowState;
			}
		}

		return friendlyURLPath;
	}

	public String getMapping() {
		return _MAPPING;
	}

	public String getPortletId() {
		return _PORTLET_ID;
	}

	public void populateParams(
		String friendlyURLPath, Map<String, String[]> params,
		Map<String, String> prpIdentifiers) {

		addParam(params, prpIdentifiers, "p_p_id", _PORTLET_ID);
		addParam(params, prpIdentifiers, "p_p_lifecycle", "0");
		addParam(params, prpIdentifiers, "p_p_mode", PortletMode.VIEW);

		int x = friendlyURLPath.indexOf("/", 1);
		int y = friendlyURLPath.indexOf("/", x + 1);

		if (y == -1) {
			y = friendlyURLPath.length();
		}

		if ((x + 1) == friendlyURLPath.length()) {
			addParam(params, prpIdentifiers, "struts_action", "/blogs/view");

			return;
		}

		String type = friendlyURLPath.substring(x + 1, y);

		if (type.equals("rss")) {
			addParam(params, prpIdentifiers, "p_p_lifecycle", "1");
			addParam(
				params, prpIdentifiers, "p_p_state",
				LiferayWindowState.EXCLUSIVE);

			addParam(params, prpIdentifiers, "struts_action", "/blogs/rss");
		}
		else if (type.equals("trackback")) {
			addParam(params, prpIdentifiers, "p_p_lifecycle", "1");
			addParam(
				params, prpIdentifiers, "p_p_state",
				LiferayWindowState.EXCLUSIVE);

			addParam(
				params, prpIdentifiers, "struts_action", "/blogs/trackback");

			type = friendlyURLPath.substring(y + 1);

			addParam(params, prpIdentifiers, getEntryIdParam(type), type);
		}
		else {
			addParam(
				params, prpIdentifiers, "struts_action", "/blogs/view_entry");

			addParam(params, prpIdentifiers, getEntryIdParam(type), type);
		}

		if (friendlyURLPath.indexOf("maximized", x) != -1) {
			addParam(params, prpIdentifiers, "p_p_state", WindowState.MAXIMIZED);
		}
	}

	protected String getEntryIdParam(String type) {
		if (Validator.isNumber(type)) {
			return "entryId";
		}
		else {
			return "urlTitle";
		}
	}

	private static final String _MAPPING = "blogs";

	private static final String _PORTLET_ID = PortletKeys.BLOGS;

}