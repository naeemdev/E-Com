
package com.appsolace.estore.SwipeRow;

import android.graphics.Canvas;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.loopeer.itemtouchhelperextension.ItemTouchHelperExtension;
import com.appsolace.estore.Adapter.AddressAdapter;
import com.appsolace.estore.Adapter.ShoppingCartAdapter;

public class ItemTouchHelperCallback extends ItemTouchHelperExtension.Callback {

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {

        return makeMovementFlags(ItemTouchHelper.UP| ItemTouchHelper.DOWN, ItemTouchHelper.START);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        ShoppingCartAdapter adapter = (ShoppingCartAdapter) recyclerView.getAdapter();
        adapter.move(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {

    }

    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override
    public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (dY != 0 && dX == 0) super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);

        if (viewHolder instanceof ShoppingCartAdapter.ItemBaseViewHolder) {

            ShoppingCartAdapter.ItemBaseViewHolder holder = (ShoppingCartAdapter.ItemBaseViewHolder) viewHolder;

            if (dX < -holder.mActionContainerParent.getWidth()) {
                dX = -holder.mActionContainerParent.getWidth();
            }

            holder.mViewContent.setTranslationX(dX);
        }
        if (viewHolder instanceof AddressAdapter.ItemBaseViewHolder) {

            AddressAdapter.ItemBaseViewHolder holder = (AddressAdapter.ItemBaseViewHolder) viewHolder;

            if (dX < -holder.mActionContainerParent.getWidth()) {
                dX = -holder.mActionContainerParent.getWidth();
            }

            holder.mViewContent.setTranslationX(dX);
        }

    }
}
