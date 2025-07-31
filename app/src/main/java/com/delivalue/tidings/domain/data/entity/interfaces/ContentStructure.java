package com.delivalue.tidings.domain.data.entity.interfaces;

import java.util.List;

public interface ContentStructure {
    String getText();
    List<PostMediaStructure> getMedia();
    List<String> getTag();
}
