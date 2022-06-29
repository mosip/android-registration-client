package io.mosip.registration.app.viewmodel;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import io.mosip.registration.app.R;
import io.mosip.registration.app.databinding.PacketListItemBinding;
import io.mosip.registration.app.viewmodel.model.RegistrationPacketModel;

import java.util.ArrayList;
import java.util.List;

public class RegistrationPacketListAdapter extends RecyclerView.Adapter<RegistrationPacketListAdapter.ViewHolder> {

    private Context mContext;
    private List<RegistrationPacketModel> list;
    private SparseBooleanArray selectedItems;
    private int selectedIndex = -1;
    private OnItemClick itemClick;

    public void setItemClick(OnItemClick itemClick) {
        this.itemClick = itemClick;
    }

    public RegistrationPacketListAdapter(Context mContext, List<RegistrationPacketModel> list) {
        this.mContext = mContext;
        this.list = list;
        selectedItems = new SparseBooleanArray();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        PacketListItemBinding bi = DataBindingUtil.inflate(LayoutInflater.from(mContext), R.layout.packet_list_item, parent, false);
        return new ViewHolder(bi);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        holder.bi.packetIdTxt.setText(list.get(position).getPacketId());
        holder.bi.statusTxt.setText(list.get(position).getPacketStatus());
        holder.bi.date.setText(list.get(position).getPacketCreatedDate());
        //Changes the activated state of this view.
        holder.bi.lytParent.setActivated(selectedItems.get(position, false));
        holder.bi.pktUploadProgress.setProgress(list.get(position).getProgress());
        holder.bi.pktUploadProgress.setVisibility(list.get(position).ProgressBarVisible() ? View.VISIBLE : View.GONE);

        holder.bi.lytParent.setOnClickListener(view -> {
            if (itemClick == null) return;
            itemClick.onItemClick(view, list.get(position), position);
        });

        holder.bi.lytParent.setOnLongClickListener(view -> {
            if (itemClick == null) {
                return false;
            } else {
                itemClick.onLongPress(view, list.get(position), position);
                return true;
            }
        });

        holder.bi.btnUpload.setOnClickListener(view -> {
            if (itemClick == null) return;
            itemClick.onItemBtnPress(view, list.get(position), position);
        });

        toggleItemColor(holder.bi, position);
    }

    /*
       This method will trigger when we we long press the item and it will change the icon of the item to check icon.
     */
    private void toggleItemColor(PacketListItemBinding bi, int position) {
        if (selectedItems.get(position, false)) {
            bi.getRoot().setBackgroundColor(mContext.getColor(R.color.btn_primary_theme1));
            if (selectedIndex == position) selectedIndex = -1;
        } else {
            bi.getRoot().setBackgroundColor(mContext.getColor(R.color.design_default_color_background));
            if (selectedIndex == position) selectedIndex = -1;
        }
    }

    /*
       This method helps you to get all selected items from the list
     */
    public List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<>(selectedItems.size());
        for (int i = 0; i < selectedItems.size(); i++) {
            items.add(selectedItems.keyAt(i));
        }
        return items;
    }

    /*
       for clearing our selection
     */
    public void clearSelection() {
        selectedItems.clear();
        notifyDataSetChanged();
    }

    /*
     this function will toggle the selection of items
     */
    public void toggleSelection(int position) {
        selectedIndex = position;
        if (selectedItems.get(position, false)) {
            selectedItems.delete(position);
        } else {
            selectedItems.put(position, true);
        }
        notifyItemChanged(position);
    }

    /*
      How many items have been selected? this method exactly the same . this will return a total number of selected items.
     */
    public int selectedItemCount() {
        return selectedItems.size();
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public RegistrationPacketModel getItem(int itemIndex) {
        return list.get(itemIndex);
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        PacketListItemBinding bi;

        public ViewHolder(@NonNull PacketListItemBinding itemView) {
            super(itemView.getRoot());
            bi = itemView;
        }
    }

    public interface OnItemClick {
        void onItemClick(View view, RegistrationPacketModel registrationPacketModel, int position);

        void onLongPress(View view, RegistrationPacketModel registrationPacketModel, int position);

        void onItemBtnPress(View view, RegistrationPacketModel registrationPacketModel, int position);
    }
}
