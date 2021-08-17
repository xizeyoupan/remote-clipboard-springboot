package com.xizeyoupan.remoteclipboard.service.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.xizeyoupan.remoteclipboard.entity.Clip;
import com.xizeyoupan.remoteclipboard.service.FileService;
import com.xizeyoupan.remoteclipboard.utils.KeyGenerator;
import org.springframework.stereotype.Service;

@Service
public class FilesServiceImpl implements FileService {

    private final Cache<String, Object> caffeineCache;

    public FilesServiceImpl(Cache<String, Object> caffeineCache) {
        this.caffeineCache = caffeineCache;
    }



    @Override
    public void put(Clip clip, byte[] bytes) {
        caffeineCache.put(KeyGenerator.keyForFile(clip), bytes);
    }

    @Override
    public void del(Clip clip) {
        caffeineCache.invalidate(KeyGenerator.keyForFile(clip));
    }

    @Override
    public byte[] get(Clip clip) {
        return (byte[]) caffeineCache.getIfPresent(KeyGenerator.keyForFile(clip));
    }
}
