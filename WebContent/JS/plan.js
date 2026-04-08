$(function() {
    // 表单提交事件
    $("#activityForm").submit(function(e) {
        e.preventDefault();
        
        // 获取用户ID（从localStorage中获取）
        var userId = localStorage.getItem("userId");
        if (!userId) {
            alert("请先登录");
            // 显示登录模态框
            $("#loginModal").addClass("active");
            return;
        }
        
        // 获取表单数据
        var activityPlace = $("#activityLocation").val();
        var activityType = $("#activityType").val();
        var startTime = $("#startTime").val();
        var endTime = $("#endTime").val();
        var activityPeople = $("#participantCount").val();
        var activityBudget = $("#budget").val();
        var phone = $("#phone").val();
        var name = $("#name").val();
        var gender = $(":radio[name='gender']:checked").val();
        var demandDescription = $("#demandDescription").val();
        
        // 构建请求数据
        var data = {
            userId: userId,
            activity_place: activityPlace,
            activity_type: activityType,
            start_time: startTime,
            end_time: endTime,
            activity_people: activityPeople,
            activity_budget: activityBudget,
            activity_progress: "0",
            requirement_desc: demandDescription || "",
            phone: phone,
            name: name,
            gender: gender
        };
        
        // 发送AJAX请求
        $.ajax({
            url: "http://localhost:8080/EventPlanning_Management/applyActivity",
            type: "POST",
            data: data,
            dataType: "json",
            success: function(response) {
                console.log("响应数据:", response);
                if (response.flag === "success" || response.flag === "SUCCESS") {
                    alert("活动需求提交成功！");
                    // 重置表单
                    $("#activityForm")[0].reset();
                } else {
                    alert("提交失败：" + (response.data || response.message || "未知错误"));
                }
            },
            error: function(xhr, status, error) {
                console.log("错误信息:", error);
                console.log("状态码:", xhr.status);
                console.log("响应文本:", xhr.responseText);
                alert("提交失败，请检查网络连接或稍后重试。错误信息：" + error + "\n状态码：" + xhr.status);
            }
        });
    });
});
