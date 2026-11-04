/*
 * Decompiled with CFR 0.152.
 */
package com.sunwayworld.framework.thymeleaf.templateresource;

import com.sunwayworld.framework.exception.UnsupportedException;
import java.util.Optional;
import org.springframework.core.io.Resource;
import org.thymeleaf.templateresource.ITemplateResource;

public abstract class GikamBaseResourceTemplateResource
implements ITemplateResource {
    private final Resource resource;
    private final String characterEncoding;

    public GikamBaseResourceTemplateResource(Resource resource, String characterEncoding) {
        this.resource = resource;
        this.characterEncoding = Optional.ofNullable(characterEncoding).orElse("utf-8");
    }

    @Override
    public String getDescription() {
        return this.getResource().getDescription();
    }

    @Override
    public String getBaseName() {
        return this.computeBaseName(this.getResource().getFilename());
    }

    @Override
    public boolean exists() {
        return this.getResource().exists();
    }

    @Override
    public ITemplateResource relative(String relativeLocation) {
        throw new UnsupportedException();
    }

    protected Resource getResource() {
        return this.resource;
    }

    protected String getCharacterEncoding() {
        return this.characterEncoding;
    }

    private String computeBaseName(String path) {
        if (path == null || path.length() == 0) {
            return null;
        }
        String basePath = path.charAt(path.length() - 1) == '/' ? path.substring(0, path.length() - 1) : path;
        int slashPos = basePath.lastIndexOf(47);
        if (slashPos != -1) {
            int dotPos = basePath.lastIndexOf(46);
            if (dotPos != -1 && dotPos > slashPos + 1) {
                return basePath.substring(slashPos + 1, dotPos);
            }
            return basePath.substring(slashPos + 1);
        }
        int dotPos = basePath.lastIndexOf(46);
        if (dotPos != -1) {
            return basePath.substring(0, dotPos);
        }
        return basePath.length() > 0 ? basePath : null;
    }
}
