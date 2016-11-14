package hu.bme.aut.exhibitionexplorer.interfaces;

/**
 * Created by Zay on 2016.11.14..
 */

public interface FavoriteTouchHelperAdapter {
    void onItemMove(int fromPosition, int toPosition);

    void onItemDismiss(int position);
}
