package hu.bme.aut.exhibitionexplorer.interfaces;

import hu.bme.aut.exhibitionexplorer.data.Artifact;

/**
 * Created by Zay on 2016.11.14..
 */

public interface OnFavoriteListener {
    void ArtifactToFavorite(Artifact artifact);

    void ArtifactRemovedFromFavorite(Artifact artifact);
}
