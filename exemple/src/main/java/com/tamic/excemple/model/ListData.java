package com.tamic.excemple.model;

import java.util.List;

/**
 * Created by tamic on 2017-06-09.
 */

public class ListData {


    public ListData(List<MusicBookCategoryListBean> musicBookCategoryList) {
        this.musicBookCategoryList = musicBookCategoryList;
    }

    public ListData() {
    }

    /**
     * id : 15
     * createDate : 1496286231000
     * modifyDate : 1496291243000
     * order : 1
     * name : 推荐1
     * treePath : ,13,
     * grade : 1
     */



    private List<MusicBookCategoryListBean> musicBookCategoryList;

    public List<MusicBookCategoryListBean> getMusicBookCategoryList() {
        return musicBookCategoryList;
    }

    public void setMusicBookCategoryList(List<MusicBookCategoryListBean> musicBookCategoryList) {
        this.musicBookCategoryList = musicBookCategoryList;
    }

    public static class MusicBookCategoryListBean {
        private int id;
        private long createDate;
        private long modifyDate;
        private int order;
        private String name;
        private String treePath;
        private int grade;

        public MusicBookCategoryListBean() {
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public long getCreateDate() {
            return createDate;
        }

        public void setCreateDate(long createDate) {
            this.createDate = createDate;
        }

        public long getModifyDate() {
            return modifyDate;
        }

        public void setModifyDate(long modifyDate) {
            this.modifyDate = modifyDate;
        }

        public int getOrder() {
            return order;
        }

        public void setOrder(int order) {
            this.order = order;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getTreePath() {
            return treePath;
        }

        public void setTreePath(String treePath) {
            this.treePath = treePath;
        }

        public int getGrade() {
            return grade;
        }

        public void setGrade(int grade) {
            this.grade = grade;
        }

        @Override
        public String toString() {
            return "MusicBookCategoryListBean{" +
                    "id=" + id +
                    ", createDate=" + createDate +
                    ", modifyDate=" + modifyDate +
                    ", order=" + order +
                    ", name='" + name + '\'' +
                    ", treePath='" + treePath + '\'' +
                    ", grade=" + grade +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "ListData{" +
                "musicBookCategoryList=" + musicBookCategoryList +
                '}';
    }
}
