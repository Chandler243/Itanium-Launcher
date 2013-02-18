/*
 * This file is part of Technic Launcher.
 *
 * Copyright (c) 2013-2013, Technic <http://www.technicpack.net/>
 * Technic Launcher is licensed under the Spout License Version 1.
 *
 * Technic Launcher is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * In addition, 180 days after any changes are published, you can use the
 * software, incorporating those changes, under the terms of the MIT license,
 * as described in the Spout License Version 1.
 *
 * Technic Launcher is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License,
 * the MIT license and the Spout License Version 1 along with this program.
 * If not, see <http://www.gnu.org/licenses/> for the GNU Lesser General Public
 * License and see <http://www.spout.org/SpoutDevLicenseV1.txt> for the full license,
 * including the MIT license.
 */

package org.spoutcraft.launcher.technic.rest;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.apache.commons.io.IOUtils;
import org.codehaus.jackson.annotate.JsonProperty;
import org.codehaus.jackson.map.ObjectMapper;
import org.spoutcraft.launcher.Settings;
import org.spoutcraft.launcher.api.Launcher;
import org.spoutcraft.launcher.exceptions.RestfulAPIException;
import org.spoutcraft.launcher.rest.Versions;
import org.spoutcraft.launcher.technic.CustomInfo;
import org.spoutcraft.launcher.technic.RestInfo;
import org.spoutcraft.launcher.technic.rest.pack.RestModpack;
import org.spoutcraft.launcher.util.MirrorUtils;

public class RestAPI {
	private static RestAPI TECHNIC;

	private final static ObjectMapper mapper = new ObjectMapper();

	private final String restURL;
	private final String restInfoURL;
	private final String cacheURL;
	private final String modURL;

	private String mirrorURL;
	private Modpacks modpacks;

	public RestAPI(String url) {
		restURL = url;
		restInfoURL = restURL + "modpack/";
		cacheURL = restURL + "cache/";
		modURL = cacheURL + "mod/";
		try {
			modpacks = setupModpacks();
			modpacks.setRest(this);
			mirrorURL = modpacks.getMirrorURL();
		} catch (RestfulAPIException e) {
			Launcher.getLogger().log(Level.SEVERE, "Unable to connect to the Rest API at " + url + " Running Offline instead.", e);
			mirrorURL = "";
		}
	}

	public static Set<String> getDefaults() {
		Modpacks packs = getDefault().getModpacks();
		if (packs != null) {
			return packs.getMap().keySet();
		} else {
			return Collections.emptySet();
		}
	}

	public static RestAPI getDefault() {
		if (TECHNIC == null) {
			TECHNIC = new RestAPI("http://api.technicraft.cz/");
		}

		return TECHNIC;
	}

	public String getRestURL() {
		return restURL;
	}

	public String getModDownloadURL(String mod, String build) {
		return getMirrorURL() + "mods/" + mod + "/" + mod + "-" + build + ".zip";
	}

	public String getModMD5URL(String mod, String build) {
		return modURL + mod + "/" + build + "/MD5";
	}

	public String getModpackURL(String modpack, String build) {
		return restInfoURL + modpack + "/build/" + build;
	}

	public String getModpackMD5URL(String modpack) {
		return restInfoURL + modpack + "/MD5";
	}

	public String getModpackInfoURL(String modpack) {
		return restInfoURL + modpack;
	}

	public String getModpackImgURL(String modpack) {
		return getMirrorURL() + modpack + "/resources/logo_180.png";
	}

	public String getModpackBackgroundURL(String modpack) {
		return getMirrorURL() + modpack + "/resources/background.png";
	}

	public String getModpackIconURL(String modpack) {
		return getMirrorURL() + modpack + "/resources/icon.png";
	}

	public static String getCustomPackURL(String modpack) {
		return "http://beta.technicpack.net/api/modpack/" + modpack;
	}

	public static String getDownloadCountURL(String modpack) {
		if (Settings.isPackCustom(modpack)) {
			return getCustomPackURL(modpack) + "/download";
		}
		return getDefault().getRestURL() + "";
	}

	public static String getRunCountURL(String modpack) {
		if (Settings.isPackCustom(modpack)) {
			return getCustomPackURL(modpack) + "/run";
		}
		return getDefault().getRestURL() + "";
	}

	private Modpacks setupModpacks() throws RestfulAPIException {
		Modpacks result = getRestObject(Modpacks.class, restInfoURL);
		result.setRest(this);
		return result;
	}

	public Modpacks getModpacks() {
		return modpacks;
	}

	public String getMirrorURL() {
		return mirrorURL;
	}

	public List<RestInfo> getRestInfos() throws RestfulAPIException {
		return modpacks.getModpacks();
	}

	public String getModMD5(String mod, String build) throws RestfulAPIException {
		TechnicMD5 result = getRestObject(TechnicMD5.class, getModMD5URL(mod, build));
		return result.getMD5();
	}

	public RestModpack getModpack(RestInfo modpack, String build) throws RestfulAPIException {
		RestModpack result = getRestObject(RestModpack.class, getModpackURL(modpack.getName(), build));
		result.setRest(this);
		return result.setInfo(modpack, build);
	}

	public RestInfo getModpackInfo(String modpack) throws RestfulAPIException {
		RestInfo result = getRestObject(RestInfo.class, getModpackInfoURL(modpack));
		result.setRest(this);
		result.init();
		String display = modpacks.getDisplayName(modpack);
		if (display != null) {
			result.setDisplayName(display);
		}
		return result;
	}

	public static CustomInfo getCustomModpack(String packURL) throws RestfulAPIException {
		CustomInfo info = getRestObject(CustomInfo.class, packURL);
		info.init();
		return info;
	}

	public String getLatestBuild(String modpack) throws RestfulAPIException {
		return getModpackInfo(modpack).getLatest();
	}

	public String getRecommendedBuild(String modpack) throws RestfulAPIException {
		return getModpackInfo(modpack).getRecommended();
	}

	public String getModpackMD5(String modpack) throws RestfulAPIException {
		TechnicMD5 result = getRestObject(TechnicMD5.class, getModpackMD5URL(modpack));
		return result.getMD5();
	}
	
	public static int getLatestLauncherBuild(String stream) throws RestfulAPIException {
		LauncherBuild result = getRestObject(LauncherBuild.class, "http://beta.technicpack.net/api/launcher/version/" + stream);
		return result.getLatestBuild();
	}
	
	public static String getLauncherDownloadURL(int version, Boolean isJar) throws RestfulAPIException {
		String ext = null;
		if (isJar) {
			ext = "jar";
		} else {
			ext = "exe";
		}
		
		String url = "http://beta.technicpack.net/api/launcher/url/" + version + "/" + ext;
		LauncherURL result = getRestObject(LauncherURL.class, url);
		return result.getLauncherURL();
	}

	@SuppressWarnings("unchecked")
	public static <T extends RestObject> T getRestObject(Class<T> restObject, String url) throws RestfulAPIException {
		InputStream stream = null;
		try {
			stream = new URL(url).openStream();
			RestObject result = mapper.readValue(stream, restObject);
			if (result.hasError()) {
				throw new RestfulAPIException("Error in json response: " + result.getError());
			}

			return (T) result;
		} catch (IOException e) {
			throw new RestfulAPIException("Error accessing URL [" + url + "]", e);
		} finally {
			IOUtils.closeQuietly(stream);
		}
	}
 
	public static String getMinecraftURL(String user) {
		return "http://s3.amazonaws.com/MinecraftDownload/minecraft.jar?user=" + user + "&ticket=1";
	}

	public static String getPatchURL(Modpack build) {
		String mirrorURL = "Patches/Minecraft/minecraft_";
		mirrorURL += Versions.getLatestMinecraftVersion();
		mirrorURL += "-" + build.getMinecraftVersion() + ".patch";
		String fallbackURL = "http://get.spout.org/patch/minecraft_";
		fallbackURL += Versions.getLatestMinecraftVersion();
		fallbackURL += "-" + build.getMinecraftVersion() + ".patch";
		return MirrorUtils.getMirrorUrl(mirrorURL, fallbackURL);
	}

	private static class TechnicMD5 extends RestObject {
		@JsonProperty("MD5")
		String md5;

		public String getMD5() {
			return md5;
		}
	}
	
	private static class LauncherBuild extends RestObject {
		@JsonProperty("LatestBuild")
		int latestBuild;
		
		public int getLatestBuild() {
			return latestBuild;
		}
	}
	
	private static class LauncherURL extends RestObject {
		@JsonProperty("URL")
		String launcherURL;
		
		public String getLauncherURL() {
			return launcherURL;
		}
	}
}
