package com.appcatalog.catalog.dto;

import lombok.Data;

import java.util.List;

@Data
public class NexusApiResponse {

  private List<NexusArtifact> items;
  private String continuationToken;
}
