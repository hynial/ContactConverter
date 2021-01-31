package com.hynial.biz.ibuild;

public interface Builder {
    default boolean validateBeforeBuild(){
        return true;
    }

    default void buildLogic() {
        if(validateBeforeBuild()){
            build();
        }
    }

    void build();
}
