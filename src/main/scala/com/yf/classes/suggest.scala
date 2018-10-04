package com.yf.classes

object suggest {

  val nameMap = Map(("1型糖尿病","1xingtangniaobing_"),("2型糖尿病","2xingtangniaobing_"),("B细胞淋巴瘤","bxibaolinbaliu_"), ("HIV感染风险","hiv_"),
    ("IgA肾病","igashenbing_"),("阿尔兹海默病","aerzihaimobing_"),("白癜风","baidianfeng_"), ("白塞病","baisaibing_"), ("瘢痕疙瘩","banhengeda_"),
    ("鼻咽癌","biyanai_"),("丙肝病毒感染风险","binganbingduganran_"),("川崎病","chuanqibing_"),("垂体腺瘤","chuitixianliu_"),("痤疮","cuochuang_"),
    ("胆结石","danjieshi_"),("胆囊癌","dannangai_"),("癫痫","dianxian_"),("多发性骨髓瘤","duofaxinggusuiliu_"),("多发性硬化症","duofaxingyinghuazheng_"),
    ("多囊卵巢综合征","duonangruanchaozonghezheng_"),("额颞叶痴呆","enieyechidai_"),("非梗阻性无精子症","feigenxingwujingzizheng_"),
    ("非酒精性脂肪肝","feijiujingxingzhifanggan_"),("肺癌","feiai_"),("肺气肿","feiqizhong_"),("腹主动脉瘤","fuzhudongmailiu_"),("肝细胞癌","ganxibaoai_"),
    ("高甘油三酯血症","ggysanzhixuezheng_"),("高血压","gaoxueya_"),("睾丸癌","gaowanai_"),("格雷夫斯病","geleifusibing_"),("宫颈癌","gongjingai_"),
    ("孤独症谱系障碍","guduzhengpuxizhangai_"),("骨关节炎","guguanjieyan_"),("冠心病","guanxinbing_"),("过敏性鼻炎","guominxingbiyan_"),
    ("过敏性皮炎","guominxingpiyan_"),("黑色素瘤","heisesuliu_"),("喉癌","houai_"),("霍奇金淋巴瘤","huoqilinbaliu_"),("肌萎缩侧索硬化","jiweisuocesuoyinghua_"),
    ("基底细胞癌","jidixibaoai_"),("急性淋巴细胞白血病","jixinglinbaxibaobxb_"),("甲状腺癌","jiazhuangxianai_"),
    ("甲状腺功能减退症","jiazhuangxiangongnengshuaitui_"),("胶质瘤","zhiliuai_"),("胶质母细胞瘤","jiaozhimuxbl_"),("结核杆菌感染风险","jieheganjunganran_"),
    ("结直肠癌","jiezhichangai_"),("进行性核上性麻痹","jxxheshangxingmabi_"),("近视","jinshi_"),("精神分裂症","jingshenfenliezheng_"),("菌血症","junxuezheng_"),
    ("克罗恩病","keluoenbing_"),("口咽癌","kouqiangai_"),("溃疡性结肠炎","kuiyangxingjiechangyan_"),("类风湿关节炎","guanjieyan_"),("鳞状细胞癌","linzhuangxibaoai_"),
    ("颅内动脉瘤","luneidongmailiu_"),("路易体病","luyitibing_"),("卵巢癌","ruanchaoai_"),("滤泡性淋巴瘤","lvpaoxinglinbaliu_"),("麻风杆菌感染风险","mafengganjun_"),
    ("慢性粒细胞白血病","manxinglixibaobxb_"),("慢性淋巴细胞白血病","manxinglinbaxibaobxb_"),("慢性肾病","manxingshenbing_"),("慢性阻塞性肺疾病","mxzsxfeijibing_"),
    ("脑卒中","naozuzhong_"),("年龄相关性黄斑变性","nlxgxhuangbanbianxing_"),("帕金森病","pajinsengbing_"),("膀胱癌","pangguangai_"),("偏头痛","piantoutong_"),
    ("前列腺癌","qianliexianai_"),("强直性脊柱炎","qiangzhixingjizhuyan_"),("青少年特发性脊柱侧凸","qsntfxjizhucetu_"),("乳糜泻","rumixie_"),("乳腺癌","ruxianai_"),
    ("神经母细胞瘤","shenjingmuxbl_"),("肾结石","shenjieshi_"),("食管鳞状细胞癌","shiguanlinzhuangxba_"),("食管腺癌","shiguanxianai_"),
    ("双相情感障碍","shuangxiangqingganzhangai_"),("特发性膜性肾病","tfxmoxingshenbing_"),("特应性皮炎","teyingxingbiyan_"),("痛风","tongfeng_"),("胃癌","weiai_"),
    ("系统性红斑狼疮","hongbanlangchuang_"),("系统性硬化症","xitongxingyinhuabing_"),("哮喘","xiaochuan_"),("心力衰竭","xinlishuaijie_"),("牙周炎","yazhouyan_"),
    ("胰腺癌","yixianai_"),("乙肝病毒感染风险","yiganbingduganran_"),("抑郁症","yiyuzheng_"),("银屑病","yinxuebing_"),("原发性闭角型青光眼","yfxbjxqingguanyan_"),
    ("原发性胆汁性肝硬化","yfxdzxganyinghua_"),("原发性干燥综合征","yuanfaxingganzaozhz_"),("原发性开角型青光眼","yfxkjxqingguangyan_"),
    ("原发性硬化性胆管炎","yfxyhxdanguanyan_"),("重症肌无力","zhongzhengjiwuli_"),("子宫肌瘤","zigongjiliu_"),("子宫内膜癌","zigongneimoai_"),
    ("子宫内膜异位症","zigongneimoyiweizheng_"),("自身免疫性肝炎1型","zishenmianyixingganyan1x_"),("阻塞性睡眠呼吸暂停","zsxshuimianhuxizhanting_"))

    val drugMap = Map(("阿司匹林","asipilin_"), ("阿替洛尔",	"atiluoer_"), ("阿托伐他汀",	"atuofatading_"), ("艾司西酞普兰",	"aisixitaipulan_"),
      ("奥氮平",	"aodanping_"), ("奥美拉唑",	"aomeilazuo_"), ("别嘌呤醇",	"biepiaolingchun_"), ("布地奈德",	"budinaide_"),
      ("布洛芬","buluofen_"), ("二甲双胍","erjiashuanggua_"), ("非诺贝特",	"feinuobeite_"), ("氟伐他汀",	"fufatating_"),
      ("氟替卡松",	"futikasong_"),("氟西汀",	"fuxiting_"), ("甲氨喋呤",	"jiaandieling_"), ("兰索拉唑",	"lansuolazuo_"), ("雷贝拉唑",	"leibeilazuo_"),
      ("利巴韦林",	"libaweilin_"), ("利培酮",	"lipeitong_"), ("氯吡格雷",	"fubigelei_"), ("氯雷他定",	"luleitading_"),
      ("孟鲁司特",	"menglusite_"), ("帕罗西汀",	"paluoxiting_"), ("泮托拉唑",	"pantuolazuo_"), ("普伐他汀",	"pufatating_"),
      ("氢氯噻嗪",	"qinlusaiqin_"), ("瑞格列奈",	"ruigelienai_"), ("沙丁胺醇",	"shadinganchun_"), ("沙美特罗",	"shameiteluo_"),
      ("舍曲林",	"shequlin_"), ("西酞普兰",	"xitaipulan_"), ("辛伐他汀",	"xinfatating_"), ("依那普利",	"yinapuli_"))


  val bodyMap = Array(("咖啡因代谢能力",385f,336,1),("酒精成瘾风险",361.24f,337,2),("烟草成瘾风险",361.24f,338,3),
    ("酒精代谢能力",258f,339,2),("苦味敏感度",392f,340,3),("咸味敏感度",376f,341,2),("夜猫子倾向",376f,342,3),("基因身高",392f,343,2),
    ("端粒长度",419f,344,1),("耐力",392f,346,3), ("爆发力",392f,347,2),("饮食对体重的影响",1f,348,1),("运动对体重的影响",409f,350,3),
    ("体脂率偏高风险",392f,351,2),("运动后心率改善量",392f,352,3), ("运动后最大摄氧量提升量",392f,353,2),("肩袖损伤风险",392f,354,3),
    ("前十字韧带损伤风险",361.24f,355,2),("跟腱损伤风险",392f,356,3),("钙营养需求",296f,358,3), ("镁营养需求",328f,359,2),
    ("铁营养需求",296f,360,3),("硒营养需求",280f,361,2),("维生素A",215f,362,3),("维生素D",280f,363,2),("维生素E",216f,364,3),
    ("叶酸",216f,365,2), ("维生素B6",280f,366,3),("维生素B12",296f,367,2),("n-6多不饱和脂肪酸",312f,368,3),("n-3多不饱和脂肪酸",296f,370,3),
    ("皮肤抗晒能力",392f,374,3), ("皮肤抗皱能力",360f,375,2),("皮肤抗氧化能力",345f,376,3),("皮肤保湿能力",376f,377,2))

  val bodynumsMap = Map(("咖啡因代谢能力",1),("酒精成瘾风险",2),("烟草成瘾风险",3), ("酒精代谢能力",4),("苦味敏感度",5),
    ("咸味敏感度",6),("夜猫子倾向",7),("基因身高",8), ("端粒长度",9),("耐力",10), ("爆发力",11),("饮食对体重的影响",12),("运动对体重的影响",13),
    ("体脂率偏高风险",14),("运动后心率改善量",15), ("运动后最大摄氧量提升量",16),("肩袖损伤风险",17), ("前十字韧带损伤风险",18),("跟腱损伤风险",19),
    ("钙营养需求",20), ("镁营养需求",21), ("铁营养需求",22),("硒营养需求",23),("维生素A",24),("维生素D",25),("维生素E",26),
    ("叶酸",27), ("维生素B6",28),("维生素B12",29),("n-6多不饱和脂肪酸",30),("n-3多不饱和脂肪酸",31),
    ("皮肤抗晒能力",32), ("皮肤抗皱能力",33),("皮肤抗氧化能力",34),("皮肤保湿能力",35),("碳水化合物代谢水平",36),
    ("蛋白质代谢水平",37),("脂质代谢水平",38))

  val corporietyMap = Map(("咖啡因代谢能力","kafeiyindaixienengli_"),("酒精成瘾风险","jiujingchengyinfengxian_"),("烟草成瘾风险","yancaochengyinfengxian_"), ("酒精代谢能力","jiujingdaixienengli_"),("苦味敏感度","kuweimingandu_"),
    ("咸味敏感度","xianweimingandu_"),("夜猫子倾向","yemaoziqingxiang_"),("基因身高","jiyinshengao_"), ("端粒长度","duanlichangdu_"),("耐力","naili_"), ("爆发力","baofali_"),("碳水化合物代谢水平","yinshiduitizhong_"),("蛋白质代谢水平","yinshiduitizhong_"),("脂质代谢水平","yinshiduitizhong_"),("运动对体重的影响","yundongduitizhong_"),
    ("体脂率偏高风险","tizhilvpiangaofengxian_"),("运动后心率改善量","yundonghouxinlv_"), ("运动后最大摄氧量提升量","yundonghouzuidasheyang_"),("肩袖损伤风险","jianxiusunshangfengxian_"), ("前十字韧带损伤风险","qianshizirendaisunshang_"),("跟腱损伤风险","genjiansunshang_"),
    ("钙营养需求","gaiyingyangxuqiu_"), ("镁营养需求","meiyingyangxuqiu_"), ("铁营养需求","tieyingyangxuqiu_"),("硒营养需求","xiyingyangxuqiu_"),("维生素A","weishengsua_"),("维生素D","weishengsud_"),("维生素E","weishengsue_"),
    ("叶酸","yesuan_"), ("维生素B6","weishengsub6_"),("维生素B12","weishengsub12_"),("n-6多不饱和脂肪酸","n-6duobubaohezhifangsuan_"),("n-3多不饱和脂肪酸","n-3duobubaohezhifangsuan_"),
    ("皮肤抗晒能力","pifukangshainengli_"), ("皮肤抗皱能力","pifukangzhounengli_"),("皮肤抗氧化能力","pifukangyanghuanengli_"),("皮肤保湿能力","pifubaoshinengli_"))

  val line = Array(681, 665, 649, 633, 617, 601, 585, 569, 553, 537, 521, 505, 489, 473, 457, 441, 425, 409, 393, 377, 361, 345, 329,
    313, 297, 281, 265, 249, 233, 217, 201, 185, 169, 153, 137, 121, 105, 89, 73, 57, 41)



}
