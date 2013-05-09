package com.arcbees.ide.plugin.eclipse.domain;

import java.util.List;

public class ArchetypeCollection {
  
  private String nextPageToken;
  private int total;
  private String kind;
  private String etag;
  private List<Archetype> archetypes;
  
  public ArchetypeCollection() {
  }

  public String getNextPageToken() {
    return nextPageToken;
  }

  public void setNextPageToken(String nextPageToken) {
    this.nextPageToken = nextPageToken;
  }

  public int getTotal() {
    return total;
  }

  public void setTotal(int total) {
    this.total = total;
  }

  public String getKind() {
    return kind;
  }

  public void setKind(String kind) {
    this.kind = kind;
  }

  public String getEtag() {
    return etag;
  }

  public void setEtag(String etag) {
    this.etag = etag;
  }

  public List<Archetype> getArchetypes() {
    return archetypes;
  }

  public void setArchetypes(List<Archetype> archetypes) {
    this.archetypes = archetypes;
  }
  
}
