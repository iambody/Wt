package io.vtown.WeiTangApp.bean.bcomment.easy.mainsort;

import io.vtown.WeiTangApp.bean.BBase;

/**
 * Created by datutu on 2016/11/1.
 */

public class BSortCategory extends BBase {
    private String id;
    private String cate_name;//": "男女服装"
    private String cate_icon;//"cate_icon": "http://fs.v-town.cc/photo_sJIUzg1xAa4LEgvvAPfhXCh8l3vnYduD"
//


    public String getCate_icon() {
        return cate_icon;
    }

    public void setCate_icon(String cate_icon) {
        this.cate_icon = cate_icon;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCate_name() {
        return cate_name;
    }

    public void setCate_name(String cate_name) {
        this.cate_name = cate_name;
    }

    public BSortCategory() {
    }

    public BSortCategory(String id, String cate_name) {
        this.id = id;
        this.cate_name = cate_name;
    }
}
