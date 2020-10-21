$(function () {
    // 1、加载头部导航栏
    let navbar_url = 'disburseExpense/inc/topNav.html';
    let eq = getParamForUrl("eq");
    $('.webTop').load(navbar_url + ' .webTop > *', function () {
        let navLi = $(".webNav li");
        navLi.eq(parseInt(eq)-1).addClass("active");
        navLi.eq(parseInt(eq)-1).siblings().removeClass("active");
    });
});