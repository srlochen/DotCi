/*
The MIT License (MIT)

Copyright (c) 2014, Groupon, Inc.

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in
all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
THE SOFTWARE.
*/
package com.groupon.jenkins.dynamic.organizationcontainer;

import hudson.DescriptorExtensionList;
import hudson.Extension;
import hudson.ExtensionPoint;
import hudson.model.AbstractStatusIcon;
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.model.Hudson;

import java.io.IOException;

import org.acegisecurity.Authentication;
import org.acegisecurity.context.SecurityContextHolder;
import org.jenkinsci.plugins.GithubAuthenticationToken;
import org.kohsuke.github.GitHub;
import org.kohsuke.stapler.Stapler;

public class OrganizationGravatarIcon extends AbstractStatusIcon implements Describable<OrganizationGravatarIcon>, ExtensionPoint {

	private final transient String orgName;

	public OrganizationGravatarIcon(String orgName) {
		this.orgName = orgName;

	}

	private String getGravatarUrl() {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		if (auth instanceof GithubAuthenticationToken) {
			GitHub gh = ((GithubAuthenticationToken) auth).getGitHub();
			try {
				return gh.getOrganization(orgName).getAvatarUrl();
			} catch (IOException e) {
				// try user
				try {
					return gh.getUser(orgName).getAvatarUrl();
				} catch (IOException e1) {
					// give up
				}
			}
		}
		return null;
	}

	public String getImageOf(String size) {
		String gravatarUrl = getGravatarUrl();
		return gravatarUrl == null ? Stapler.getCurrentRequest().getContextPath() + Hudson.RESOURCE_PATH + "/images/" + size + "/folder.png" : gravatarUrl;
	}

	public String getDescription() {
		return orgName;
	}

	@Extension(ordinal = 100)
	public static class DescriptorImpl extends Descriptor<OrganizationGravatarIcon> {
		@Override
		public String getDisplayName() {
			return "Default Icon";
		}

		public static DescriptorExtensionList<OrganizationGravatarIcon, DescriptorImpl> all() {
			return Hudson.getInstance().<OrganizationGravatarIcon, DescriptorImpl> getDescriptorList(OrganizationGravatarIcon.class);
		}
	}

	public Descriptor<OrganizationGravatarIcon> getDescriptor() {
		return Hudson.getInstance().getDescriptorOrDie(getClass());
	}

}
