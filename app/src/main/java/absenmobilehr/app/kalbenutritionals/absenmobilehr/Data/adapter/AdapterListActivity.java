package absenmobilehr.app.kalbenutritionals.absenmobilehr.Data.adapter;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import absenmobilehr.app.kalbenutritionals.absenmobilehr.Data.common.clsActivityUser;
import absenmobilehr.app.kalbenutritionals.absenmobilehr.R;

public class AdapterListActivity extends RecyclerView.Adapter<AdapterListActivity.ViewHolder> {

    private Context ctx;
    private List<clsActivityUser> items;
    private OnClickListener onClickListener = null;
    private SparseBooleanArray selected_items;
    private int current_selected_idx = -1;

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView category, activityDesc, statusVerify, date, image_letter;
        public ImageView image;
        public RelativeLayout lyt_checked, lyt_image;
        public View lyt_parent;

        public ViewHolder(View view) {
            super(view);
            category = (TextView) view.findViewById(R.id.Category);
            activityDesc = (TextView) view.findViewById(R.id.ActivityDescription);
            statusVerify = (TextView) view.findViewById(R.id.statusVerify);
            date = (TextView) view.findViewById(R.id.dateRange);
            image_letter = (TextView) view.findViewById(R.id.image_letter);
            image = (ImageView) view.findViewById(R.id.image);
            lyt_checked = (RelativeLayout) view.findViewById(R.id.lyt_checked);
            lyt_image = (RelativeLayout) view.findViewById(R.id.lyt_image);
            lyt_parent = (View) view.findViewById(R.id.lyt_parent);
        }
    }

    public AdapterListActivity(Context mContext, List<clsActivityUser> items) {
        this.ctx = mContext;
        this.items = items;
        selected_items = new SparseBooleanArray();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_activity, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        final clsActivityUser activityUser = items.get(position);

        // displaying text view data
        holder.category.setText(activityUser.getTxtCategory());
        holder.activityDesc.setText(activityUser.getActivity());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        String strStartDate = sdf.format(activityUser.getDtStart());
        String strEndDate = sdf.format(activityUser.getDtEnd());
        String strVerified = "";
        if (activityUser.getDtVerified() != null){
            String strValidationDate = sdf.format(activityUser.getDtVerified());
//            holder.statusVerify.setText("Verified : "+strValidationDate);
            holder.statusVerify.setText(activityUser.getTxtStatus()+" : "+strValidationDate);
            holder.statusVerify.setTextColor(Color.GREEN);

        }else {
            holder.statusVerify.setText(activityUser.getTxtStatus());
            holder.statusVerify.setTextColor(Color.RED);
        }
        String strDateRange = strStartDate + " - " + strEndDate;
        holder.date.setText(strDateRange);
//        holder.image_letter.setText(activityU
// ser.from.substring(0, 1));

        holder.lyt_parent.setActivated(selected_items.get(position, false));

        holder.lyt_parent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onClickListener == null) return;
                onClickListener.onItemClick(v, activityUser, position);
            }
        });

        holder.lyt_parent.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onClickListener == null) return false;
                onClickListener.onItemLongClick(v, activityUser, position);
                return true;
            }
        });

        toggleCheckedIcon(holder, position);
        displayImage(holder, activityUser);

    }

    private void displayImage(ViewHolder holder, clsActivityUser clsActivityUser) {
        /*if (clsActivityUser.image != null) {
            Tools.displayImageRound(ctx, holder.image, clsActivityUser.image);
            holder.image.setColorFilter(null);
            holder.image_letter.setVisibility(View.GONE);
        } else {
            holder.image.setImageResource(R.drawable.shape_circle);
            holder.image.setColorFilter(clsActivityUser.color);
            holder.image_letter.setVisibility(View.VISIBLE);
        }*/
    }

    private void toggleCheckedIcon(ViewHolder holder, int position) {
        if (selected_items.get(position, false)) {
            holder.lyt_image.setVisibility(View.GONE);
            holder.lyt_checked.setVisibility(View.VISIBLE);
            if (current_selected_idx == position) resetCurrentIndex();
        } else {
            holder.lyt_checked.setVisibility(View.GONE);
            holder.lyt_image.setVisibility(View.VISIBLE);
            if (current_selected_idx == position) resetCurrentIndex();
        }
    }

    public clsActivityUser getItem(int position) {
        return items.get(position);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public boolean toggleSelection(int pos) {
        current_selected_idx = pos;
        boolean ret = true;
        if (selected_items.get(pos, false)) {
            selected_items.delete(pos);
            ret = true;
        } else {
            selected_items.put(pos, true);
            ret = false;
        }
        notifyItemChanged(pos);
        return ret;
    }

    public void clearSelections() {
        selected_items.clear();
        notifyDataSetChanged();
    }

    public int getSelectedItemCount() {
        return selected_items.size();
    }

    public List<Integer> getSelectedItems() {
        List<Integer> items = new ArrayList<>(selected_items.size());
        for (int i = 0; i < selected_items.size(); i++) {
            items.add(selected_items.keyAt(i));
        }
        return items;
    }

    public void removeData(int position) {
        items.remove(position);
        resetCurrentIndex();
    }

    private void resetCurrentIndex() {
        current_selected_idx = -1;
    }

    public interface OnClickListener {
        void onItemClick(View view, clsActivityUser obj, int pos);

        void onItemLongClick(View view, clsActivityUser obj, int pos);
    }
}