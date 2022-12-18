package net.nathan.goodtalk.common.widget.recycler;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import net.nathan.goodtalk.common.R;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.Unbinder;

public abstract class RecyclerAdapter<Data> extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder<Data>>
        implements View.OnClickListener, View.OnLongClickListener, AdapterCallback {
    private final List<Data> mDataList;
    private AdapterListener<Data> mListener;

    public RecyclerAdapter() {
        this(null);
    }

    public RecyclerAdapter(AdapterListener<Data> listener) {
        this(new ArrayList<Data>(), listener);
    }

    public RecyclerAdapter(List<Data> dataList, AdapterListener<Data> listener) {
        this.mDataList = dataList;
        this.mListener = listener;
    }

    /**
     * 复写默认的布局类型返回方法
     *
     * @param position
     * @return 其实返回的就是XML布局的Id
     */
    @Override
    public int getItemViewType(int position) {
        return getItemViewType(position, mDataList.get(position));
    }

    /**
     * 得到布局类型
     *
     * @param position
     * @param data
     * @return
     */
    @LayoutRes
    protected abstract int getItemViewType(int position, Data data);

    /**
     * 根据viewType创建一个特定的ViewHolder
     *
     * @param parent   RecyclerView
     * @param viewType 界面类型，约定为XML布局的Id
     * @return ViewHolder
     */
    @Override
    public ViewHolder<Data> onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        // 将Id=viewType的XML文件初始化为root View
        View root = inflater.inflate(viewType, parent, false);
        ViewHolder<Data> holder = onCreateViewHolder(root, viewType);

        // 设置View的tag为ViewHolder，双向绑定
        root.setTag(R.id.tag_recycler_holder, holder);

        root.setOnClickListener(this);
        root.setOnLongClickListener(this);

        // 界面注解绑定
        holder.unbinder = ButterKnife.bind(holder, root);
        holder.callback = this;
        return holder;
    }

    /**
     * 得到一个新的ViewHolder
     *
     * @param root     根布局
     * @param viewType 布局类型，其实就是XML的ID
     * @return ViewHolder
     */
    protected abstract ViewHolder<Data> onCreateViewHolder(View root, int viewType);

    /**
     * 绑定数据到一个具体的ViewHolder
     *
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(ViewHolder<Data> holder, int position) {
        // 拿到指定位置的数据，然后触发绑定动作
        Data data = mDataList.get(position);
        holder.bind(data);

    }

    @Override
    public int getItemCount() {
        return mDataList.size();
    }

    /**
     * 插入一条数据并通知更新
     *
     * @param data
     */
    public void add(Data data) {
        mDataList.add(data);
        notifyItemInserted(mDataList.size() - 1);
    }

    /**
     * 插入一堆的数据并通知更新
     *
     * @param dataList
     */
    public void add(Data... dataList) {
        if (dataList != null && dataList.length > 0) {
            int startPos = mDataList.size();
            Collections.addAll(mDataList, dataList);
            notifyItemRangeInserted(startPos, dataList.length);
        }
    }

    /**
     * 插入一个集合的数据并通知更新
     *
     * @param dataList
     */
    public void add(Collection<Data> dataList) {
        if (dataList != null && dataList.size() > 0) {
            int startPos = mDataList.size();
            mDataList.addAll(dataList);
            notifyItemRangeInserted(startPos, dataList.size());
        }
    }

    /**
     * 全部删除
     */
    public void clear() {
        mDataList.clear();
        // 全部更新
        notifyDataSetChanged();
    }

    public void replace(Collection<Data> dataList) {
        mDataList.clear();
        if (dataList == null || dataList.size() == 0) {
            return;
        }
        mDataList.addAll(dataList);
        notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        ViewHolder holder = (ViewHolder) v.getTag(R.id.tag_recycler_holder);
        if (mListener != null) {
            // 得到当前viewholder的位置
            int pos = holder.getAdapterPosition();
            mListener.onItemClick(holder, mDataList.get(pos));
        }
    }

    @Override
    public boolean onLongClick(View v) {
        ViewHolder holder = (ViewHolder) v.getTag(R.id.tag_recycler_holder);
        if (mListener != null) {
            // 得到当前viewholder的位置
            int pos = holder.getAdapterPosition();
            mListener.onItemLongClick(holder, mDataList.get(pos));
            return true;
        }
        return false;
    }

    /**
     * 设置适配器监听
     *
     * @param adapterListener
     */
    public void setListener(AdapterListener<Data> adapterListener) {
        this.mListener = adapterListener;
    }

    /**
     * 自定义监听器
     *
     * @param <Data>
     */
    public interface AdapterListener<Data> {
        void onItemClick(ViewHolder holder, Data data);

        void onItemLongClick(ViewHolder holder, Data data);
    }

    public static abstract class ViewHolder<Data> extends RecyclerView.ViewHolder {
        private Unbinder unbinder;
        private AdapterCallback<Data> callback;
        protected Data mData;

        public ViewHolder(View itemView) {
            super(itemView);
        }

        /**
         * 绑定数据时触发此方法
         *
         * @param data 传入的数据
         */
        void bind(Data data) {
            this.mData = data;
            onBind(data);
        }

        /**
         * 外部调用绑定数据方法后的回调，必须复写
         *
         * @param data
         */
        protected abstract void onBind(Data data);

        /**
         * Holder对自己的data进行更新操作
         *
         * @param data
         */
        public void updateData(Data data) {
            if (this.callback != null) {
                this.callback.update(data, this);
            }
        }
    }
}
