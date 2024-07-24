package com.eoi.grupo5.paginacion;

import java.util.Iterator;
import java.util.List;

public interface PaginaRespuesta<T> extends Iterable<T>{

    List<T> getContent();
    void setContent(List<T> content);
    Integer getSize();
    void setSize(Integer size);
    long getTotalSize();
    void setTotalSize(long totalSize);
    Integer getPage();
    void setPage(Integer page);
    Integer getTotalPages();
    void setTotalPages(Integer totalPages);

    @Override
    default Iterator<T> iterator() {
        return getContent().iterator();
    }
}
