package net.onpointcoding.multipleserverlists.duck;

public interface EntryListWidgetDuckProvider {
    void resetScrollPosition();

    void setRefreshCallback(Runnable callback);
}
