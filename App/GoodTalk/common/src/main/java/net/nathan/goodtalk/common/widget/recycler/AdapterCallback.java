package net.nathan.goodtalk.common.widget.recycler;

public interface AdapterCallback<Data> {
    void update(Data data, RecyclerAdapter.ViewHolder<Data> holder);
}
