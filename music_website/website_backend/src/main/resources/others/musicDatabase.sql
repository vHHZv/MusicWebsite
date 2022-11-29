# 管理员表
create table tb_admin
(
    aid        bigint primary key auto_increment, # 编号
    admin_name varchar(10) not null,              # 账户名
    passwd     varchar(20) not null               # 密码
);

# 图像表
create table tb_image
(
    image_id bigint primary key auto_increment, # 编号
    filename varchar(100),                      # uuid_文件名
    md5      char(32),                          # 图片 MD5
    deleted  int not null default 0             # 删除标志，0表示未删除
);

# 音频表
create table tb_audio
(
    audio_id bigint primary key auto_increment, # 编号
    filename varchar(100),                      # uuid_文件名
    md5      char(32),                          # 音频 MD5
    deleted  int not null default 0             # 删除标志，0表示未删除
);

# 用户表
create table tb_user
(
    uid       bigint primary key,               # 编号
    username  varchar(10) not null unique,      # 用户名
    passwd    varchar(20) not null,             # 密码
    gender    char(1)              default '男', # 性别
    phone_Num char(11),                         # 手机号
    email     varchar(30),                      # 邮箱
    birthday  date,                             # 出生日期
    avatar    bigint      not null default 1,   # 头像名
    info      text,                             # 介绍
    version   int         not null default 1,   # 乐观锁
    deleted   int         not null default 0,   # 删除标志，0表示未删除
    check ( gender = '男' or gender = '女' ),
    constraint fk_user_avatar_tb_image foreign key (avatar) references tb_image (image_id)
);

# 唱片公司
create table tb_company
(
    cid          bigint primary key,             # 编号
    company_name varchar(20) not null unique,    # 公司名
    version      int         not null default 1, # 乐观锁
    deleted      int         not null default 0  # 删除标志，0表示未删除
);

# 歌手
create table tb_singer
(
    sid         bigint primary key,             # 编号
    singer_name varchar(10) not null unique,    # 姓名
    alias       varchar(20) unique,             # 别名
    avatar      bigint      not null default 1, # 头像
    info        text,                           # 信息
    version     int         not null default 1, # 乐观锁
    deleted     int         not null default 0, # 删除标志，0表示未删除
    constraint fk_singer_avatar_tb_image foreign key (avatar) references tb_image (image_id)
);

# 歌单、专辑
create table tb_playlist
(
    pid         bigint primary key,                # 编号
    type        char(2)     not null,              # 类型
    title       varchar(10) not null unique,       # 标题
    cover       bigint      not null default 2,    # 封面
    show_level  char(2)     not null default '公开', # 展示级别
    builder     bigint,                            # 歌单创建者
    company     bigint,                            # 专辑发行公司
    singer      bigint,                            # 专辑所属歌手
    create_date date        not null,              # 创建时间
    info        varchar(100),                      # 简介
    version     int         not null default 1,    # 乐观锁
    deleted     int         not null default 0,    # 删除标志，0表示未删除
    # 当项目为歌单时，创建者不能为空；当项目为专辑时，发行公司和歌手不能为空
    check ( (type = '歌单' and builder is not null) or (type = '专辑' and company is not null and singer is not null) ),
    check ( show_level in ('轮播', '首页', '公开', '私密') ),
    constraint fk_playlist_cover_tb_image foreign key (cover) references tb_image (image_id),
    constraint fk_builder_tb_user foreign key (builder) references tb_user (uid),
    constraint fk_company_tb_company foreign key (company) references tb_company (cid),
    constraint fk_singer_tb_singer foreign key (singer) references tb_singer (sid)
);

# 单曲
create table tb_song
(
    song_id   bigint primary key,             # 编号
    song_name varchar(20) not null unique,    # 单曲名
    cover     bigint      not null default 2, # 封面
    playlist  bigint      not null unique,    # 所属专辑
    info      varchar(20),                    # 简介
    mp3       bigint      not null,           # 音频名
    lyrics    text,                           # 歌词
    version   int         not null default 1, # 乐观锁
    deleted   int         not null default 0, # 删除标志，0表示未删除
    constraint fk_song_cover_tb_image foreign key (cover) references tb_image (image_id),
    constraint fk_playlist_tb_playlist foreign key (playlist) references tb_playlist (pid),
    constraint fk_song_mp3_tb_audio foreign key (mp3) references tb_audio (audio_id)
);

# 用户关注表
create table tb_user_user
(
    origin bigint, # 关注用户 uid
    target bigint, # 被关注用户 uid
    primary key (origin, target),
    constraint fk_origin_tb_user foreign key (origin) references tb_user (uid),
    constraint fk_target_tb_user foreign key (target) references tb_user (uid)
);

# 用户收藏的歌单
create table tb_user_playlist
(
    user     bigint, # 用户 uid
    playlist bigint, # 歌单 pid
    primary key (user, playlist),
    constraint fk_user_tb_user foreign key (user) references tb_user (uid),
    constraint fk_p_tb_playlist foreign key (playlist) references tb_playlist (pid)
);

# 歌单收录的单曲
create table tb_playlist_song
(
    playlist bigint, # 专辑（歌单）pid
    song     bigint, # 单曲 song_id
    primary key (playlist, song),
    constraint fk_pl_tb_playlist foreign key (playlist) references tb_playlist (pid),
    constraint fk_song_tb_song foreign key (song) references tb_song (song_id)
);

# 歌手_单曲
create table tb_singer_song
(
    singer bigint, # 歌手 sid
    song   bigint, # 单曲 song_id
    primary key (singer, song),
    constraint fk_s_tb_singer foreign key (singer) references tb_singer (sid),
    constraint fk_s_tb_song foreign key (song) references tb_song (song_id)
);

