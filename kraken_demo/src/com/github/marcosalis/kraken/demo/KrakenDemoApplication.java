/*
 * Copyright 2013 Marco Salis - fast3r@gmail.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.github.marcosalis.kraken.demo;

import java.io.IOException;

import javax.annotation.Nonnull;

import android.app.Application;

import com.github.marcosalis.kraken.cache.bitmap.BitmapCache;
import com.github.marcosalis.kraken.cache.bitmap.BitmapCacheBuilder;
import com.github.marcosalis.kraken.cache.managers.BaseCachesManager;
import com.github.marcosalis.kraken.cache.proxies.ContentProxy;
import com.github.marcosalis.kraken.utils.DroidUtils;
import com.github.marcosalis.kraken.utils.android.DroidApplication;
import com.github.marcosalis.kraken.utils.android.LogUtils;

/**
 * {@link Application} subclass that initializes caches and holds a
 * {@link BaseCachesManager}.
 * 
 * @since 1.0
 * @author Marco Salis
 */
public class KrakenDemoApplication extends DroidApplication {

	public enum CacheId {
		BITMAPS_130, // cache for 130x130 images
		BITMAPS_320; // cache for 320x320 images
	}

	private static KrakenDemoApplication mInstance;

	/**
	 * Returns the application singleton.
	 */
	public static KrakenDemoApplication get() {
		return mInstance;
	}

	private static void setInstance(KrakenDemoApplication instance) {
		mInstance = instance;
	}

	private final BaseCachesManager<CacheId> mCachesManager;

	public KrakenDemoApplication() {
		// instantiate the cache manager
		mCachesManager = new BaseCachesManager<CacheId>(DroidUtils.CPU_CORES);
	}

	@Override
	public void onCreate() {
		setInstance(this);
		super.onCreate();

		// initialize caches
		final BitmapCacheBuilder builder130 = new BitmapCacheBuilder(this);
		builder130.maxMemoryCachePercentage(10) // 10% of available memory
				.memoryCacheLogName("BITMAPS_130") //
				.diskCachePurgeableAfter(DroidUtils.DAY) //
				.diskCacheDirectoryName("bitmaps130");

		final BitmapCacheBuilder builder320 = new BitmapCacheBuilder(this);
		builder320.maxMemoryCachePercentage(10) // 10% of available memory
				.memoryCacheLogName("BITMAPS_320") //
				.diskCachePurgeableAfter(DroidUtils.DAY) //
				.diskCacheDirectoryName("bitmaps320");

		// build caches and register them in the manager
		buildAndRegisterCache(CacheId.BITMAPS_130, builder130);
		buildAndRegisterCache(CacheId.BITMAPS_320, builder320);
	}

	@Nonnull
	public ContentProxy getCache(@Nonnull CacheId id) {
		return mCachesManager.getContent(id);
	}

	@Nonnull
	public BaseCachesManager<CacheId> getCachesManager() {
		return mCachesManager;
	}

	private void buildAndRegisterCache(@Nonnull CacheId cacheId, @Nonnull BitmapCacheBuilder builder) {
		try {
			final BitmapCache cache = builder.build();
			mCachesManager.registerContent(cacheId, cache);
		} catch (IOException e) {
			LogUtils.logException(e);
		}
	}

}