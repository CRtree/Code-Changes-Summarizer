package com.samuel.zuo.entity;

import java.util.List;

/**
 * description: OllamaModelInfo
 * date: 2024/11/7 17:11
 * author: samuel_zuo
 * version: 1.0
 */
public class OllamaModelInfo {
    private String name;
    private String modified_at;
    private long size;
    private String digest;
    private Details details;

    static class Details {
        private String format;
        private String family;
        private List<String> families;
        private String parameter_size;
        private String quantization_level;

        public String getFormat() {
            return format;
        }

        public void setFormat(String format) {
            this.format = format;
        }

        public String getFamily() {
            return family;
        }

        public void setFamily(String family) {
            this.family = family;
        }

        public List<String> getFamilies() {
            return families;
        }

        public void setFamilies(List<String> families) {
            this.families = families;
        }

        public String getParameter_size() {
            return parameter_size;
        }

        public void setParameter_size(String parameter_size) {
            this.parameter_size = parameter_size;
        }

        public String getQuantization_level() {
            return quantization_level;
        }

        public void setQuantization_level(String quantization_level) {
            this.quantization_level = quantization_level;
        }
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getModified_at() {
        return modified_at;
    }

    public void setModified_at(String modified_at) {
        this.modified_at = modified_at;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getDigest() {
        return digest;
    }

    public void setDigest(String digest) {
        this.digest = digest;
    }

    public Details getDetails() {
        return details;
    }

    public void setDetails(Details details) {
        this.details = details;
    }
}
