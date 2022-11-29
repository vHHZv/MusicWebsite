# 管理员表
create table tb_admin
(
    aid        bigint primary key auto_increment, # 编号
    admin_name varchar(10) unique not null,       # 账户名
    passwd     varchar(20)        not null        # 密码
);

# 图像表
create table tb_image
(
    image_id bigint primary key auto_increment, # 编号
    filename varchar(100),                      # uuid_文件名
    md5      char(32),                          # 图片 MD5
    info     text,                              # 描述信息
    deleted  int not null default 0,            # 删除标志，0表示未删除
    index image_index_md5 (md5)
);

# 音频表
create table tb_audio
(
    audio_id bigint primary key auto_increment, # 编号
    filename varchar(100),                      # uuid_文件名
    md5      char(32),                          # 音频 MD5
    info     text,                              # 描述信息
    deleted  int not null default 0,            # 删除标志，0表示未删除
    index audio_index_md5 (md5)
);

# 用户表
create table tb_user
(
    uid       bigint primary key auto_increment, # 编号
    username  varchar(10) not null unique,       # 用户名
    passwd    varchar(20) not null,              # 密码
    gender    char(1)              default '男',  # 性别
    phone_Num char(11),                          # 手机号
    email     varchar(30),                       # 邮箱
    birthday  date,                              # 出生日期
    avatar    bigint      not null default 1,    # 头像名
    info      text,                              # 介绍
    deleted   int         not null default 0,    # 删除标志，0表示未删除
    check ( gender = '男' or gender = '女' ),
    constraint fk_user_avatar_tb_image foreign key (avatar) references tb_image (image_id)
);

# 歌手
create table tb_singer
(
    sid         bigint primary key auto_increment, # 编号
    singer_name varchar(10) not null unique,       # 姓名
    alias       varchar(20) unique,                # 别名
    avatar      bigint      not null default 1,    # 头像
    info        text,                              # 信息
    deleted     int         not null default 0,    # 删除标志，0表示未删除
    constraint fk_singer_avatar_tb_image foreign key (avatar) references tb_image (image_id)
);

# 单曲
create table tb_song
(
    song_id   bigint primary key auto_increment, # 编号
    song_name varchar(20) not null unique,       # 单曲名
    cover     bigint      not null default 2,    # 封面
    info      varchar(20),                       # 简介
    mp3       bigint      not null,              # 音频名
    lyrics    text,                              # 歌词
    deleted   int         not null default 0,    # 删除标志，0表示未删除
    constraint fk_song_cover_tb_image foreign key (cover) references tb_image (image_id),
    constraint fk_song_mp3_tb_audio foreign key (mp3) references tb_audio (audio_id)
);

# 用户收藏的单曲
create table tb_user_song
(
    user bigint, # 用户 uid
    song bigint, # 单曲 song_id
    primary key (user, song),
    constraint fk_user_tb_user foreign key (user) references tb_user (uid) on delete cascade,
    constraint fk_p_tb_song foreign key (song) references tb_song (song_id) on delete cascade
);

# 歌手_单曲
create table tb_singer_song
(
    singer bigint, # 歌手 sid
    song   bigint, # 单曲 song_id
    primary key (singer, song),
    constraint fk_s_tb_singer foreign key (singer) references tb_singer (sid) on delete cascade,
    constraint fk_s_tb_song foreign key (song) references tb_song (song_id) on delete cascade
);