// 员工职位功能管理
$(function() {
    // 检查当前页面是否为员工版页面
    function isEmployeePage() {
        const currentUrl = window.location.pathname;
        // 员工版页面通常位于html目录下，且文件名包含employee、material、equipment等关键字
        // 或者检查是否为employee.html、material_management.html等员工版页面
        const employeePageKeywords = ['employee', 'material', 'equipment', 'manage_activity', 'personnel', 'salary', 'permission', 'user_info', 'user_contact', 'approval', 'attendance', 'finance', 'reimbursement', 'calculation', 'warehouse'];
        return employeePageKeywords.some(keyword => currentUrl.includes(keyword));
    }
    
    // 只有在员工版页面上才执行员工版导航栏逻辑
    if (isEmployeePage()) {
        // 职位与功能的对应关系
        var postFunctions = {
            "用户管理员": [
                { name: "员工注册批准", url: "employee_registration_approval.html" },
                { name: "员工权限管理", url: "permission_management.html" },
                { name: "用户信息管理", url: "user_info_management.html" }
            ],
            "业务管理员": [
                { name: "管理用户活动申请", url: "manage_activity_applications.html" },
                { name: "用户联系方式", url: "user_contact.html" },
                { name: "人员安排", url: "personnel_arrangement.html" }
            ],
            "仓库管理员": [
                { name: "管理用户活动申请", url: "manage_activity_applications.html" },
                { name: "物料/设备租借", url: "material_equipment_management.html" },
                { name: "仓库详情", url: "warehouse_management.html" }
            ],
            "会计": [
                { name: "计算每个人薪资", url: "salary_calculation.html" },
                { name: "活动收支", url: "activity_finance.html" },
                { name: "报销申请", url: "expense_reimbursement.html" }
            ],
            "工人": [
                { name: "考勤（工作安排）", url: "attendance.html" }
            ]
        };
        
        // 通用功能，所有员工都有
        var commonFunctions = [
            { name: "员工主页", url: "employee.html" },
            { name: "我的薪资", url: "salary.html" },
            { name: "员工花名册", url: "employee_list.html" },
            { name: "审批/报销", url: "approval.html" }
        ];
        
        // 获取用户职位
        function getUserPost() {
            return localStorage.getItem("post") || "";
        }
        
        // 根据职位生成导航菜单
        function generateNavMenu() {
            var post = getUserPost();
            var navMenu = $(".nav-menu");
            var currentUrl = window.location.pathname;
            
            // 清空现有菜单，移除所有静态菜单项
            navMenu.empty();
            
            // 标记当前是否为专属功能页面
            var isSpecialFunctionPage = false;
            var currentSpecialFunction = null;
            
            // 先检查是否为职位专属功能页面
            if(post && postFunctions[post] && postFunctions[post].length > 0) {
                for(var i = 0; i < postFunctions[post].length; i++) {
                    var func = postFunctions[post][i];
                    // 精确匹配URL：检查整个URL的最后一部分是否完全等于func.url
                    // 使用split('/').pop()获取URL的最后一部分，确保精确匹配
                    var urlLastPart = currentUrl.split('/').pop();
                    if(urlLastPart === func.url) {
                        isSpecialFunctionPage = true;
                        currentSpecialFunction = func;
                        break;
                    }
                }
            }
            
            // 添加通用功能菜单
            commonFunctions.forEach(function(func) {
                // 精确匹配URL：检查整个URL的最后一部分是否完全等于func.url
                var urlLastPart = currentUrl.split('/').pop();
                var isActive = urlLastPart === func.url;
                var li = $('<li></li>');
                var a = $('<a></a>')
                    .attr('href', func.url)
                    .text(func.name);
                if(isActive) {
                    a.addClass('active');
                }
                li.append(a);
                navMenu.append(li);
            });
            
            // 添加职位专属功能
            if(post && postFunctions[post] && postFunctions[post].length > 0) {
                if(postFunctions[post].length === 1) {
                    // 如果只有一个专属功能，直接显示为菜单项
                    var func = postFunctions[post][0];
                    var urlLastPart = currentUrl.split('/').pop();
                    var isActive = urlLastPart === func.url;
                    var li = $('<li></li>');
                    var a = $('<a></a>')
                        .attr('href', func.url)
                        .text(func.name);
                    if(isActive) {
                        a.addClass('active');
                    }
                    li.append(a);
                    navMenu.append(li);
                } else {
                    // 如果有多个专属功能，显示为"其他"下拉菜单
                    // 创建下拉菜单容器
                    var dropdownLi = $('<li class="dropdown"></li>');
                    
                    // 主菜单项
                    var dropdownA = $('<a href="#" class="dropdown-toggle"></a>')
                        .text('其他');
                    
                    // 如果当前是专属功能页面，给"其他"菜单项添加active状态
                    if(isSpecialFunctionPage) {
                        dropdownA.addClass('active');
                    }
                    
                    // 下拉菜单内容
                    var dropdownMenu = $('<ul class="dropdown-menu"></ul>');
                    
                    // 添加职位专属功能到下拉菜单
                    postFunctions[post].forEach(function(func) {
                        // 精确匹配URL：检查整个URL的最后一部分是否完全等于func.url
                        var urlLastPart = currentUrl.split('/').pop();
                        var isActive = urlLastPart === func.url;
                        var dropdownItem = $('<li class="dropdown-item"></li>');
                        var dropdownLink = $('<a href="' + func.url + '"></a>')
                            .text(func.name);
                        if(isActive) {
                            dropdownLink.addClass('active');
                        }
                        dropdownItem.append(dropdownLink);
                        dropdownMenu.append(dropdownItem);
                    });
                    
                    // 组装下拉菜单
                    dropdownLi.append(dropdownA);
                    dropdownLi.append(dropdownMenu);
                    navMenu.append(dropdownLi);
                }
            }
        }
        
        // 更新个人信息显示
        function updatePersonalInfo() {
            var post = getUserPost();
            var name = localStorage.getItem("name") || "员工";
            
            // 更新欢迎信息
            $('.section-title').text('欢迎回来，' + name + '！');
            
            // 更新个人信息卡片
            var infoItem = $('.info-item:contains("职位：")');
            if(infoItem.length > 0) {
                infoItem.find('.info-value').text(post || '暂无职位信息');
            }
            
            // 更新姓名显示
            var nameItem = $('.info-item:contains("姓名：")');
            if(nameItem.length > 0) {
                nameItem.find('.info-value').text(name);
            }
        }
        
        // 初始化员工功能菜单
        function initEmployeeMenu() {
            generateNavMenu();
            updatePersonalInfo();
            
            // 更新导航栏用户信息
            updateNavbarUserInfo();
        }
        
        // 更新导航栏用户信息
        function updateNavbarUserInfo() {
            // 检查用户是否已经登录
            var user = JSON.parse(localStorage.getItem("user")) || {};
            var name = localStorage.getItem("name") || user.name || user.username;
            
            // 如果用户没有登录，不显示用户信息，而是显示登录按钮
            if (!name) {
                // 清空导航栏中的用户信息
                $("#navbarUserInfo").removeClass("active").empty();
                
                // 显示登录按钮
                $("#headerLoginBtn").show();
                
                return;
            }
            
            var avatarSrc = user.avatar || "../IMG/默认头像webp.webp";
            
            // 隐藏登录按钮
            $("#headerLoginBtn").hide();
            
            // 显示用户信息
            $("#navbarUserInfo").html(
                '<div class="user-info" id="userInfoDropdown">' +
                    '<img class="user-avatar" src="' + avatarSrc + '" alt="用户头像">' +
                    '<span class="user-name">' + name + '</span>' +
                    '<div class="dropdown-menu">' +
                        '<button class="dropdown-item" id="changeInfoBtn">修改信息</button>' +
                        '<button class="dropdown-item" id="changePasswordBtn">修改密码</button>' +
                        '<button class="dropdown-item" id="planProgressBtn">方案进度</button>' +
                        '<div class="dropdown-divider"></div>' +
                        '<button class="dropdown-item" id="logoutBtn">退出登录</button>' +
                    '</div>' +
                '</div>'
            );
            
            // 绑定悬停事件处理 - 绑定到用户信息元素
            var hoverTimer;
            $("#userInfoDropdown").hover(
                function() {
                    clearTimeout(hoverTimer);
                    $(this).find(".dropdown-menu").addClass("show");
                },
                function() {
                    var $this = $(this);
                    hoverTimer = setTimeout(function() {
                        $this.find(".dropdown-menu").removeClass("show");
                    }, 200);
                }
            );
            
            // 绑定用户信息点击事件
            $(document).on('click', '#userInfoDropdown', function(e) {
                e.preventDefault();
                e.stopPropagation();
                $(this).find(".dropdown-menu").toggleClass("show");
            });
            
            // 点击页面其他地方关闭下拉菜单
            $(document).on('click', function(e) {
                if (!$(e.target).closest('#userInfoDropdown').length) {
                    $('#userInfoDropdown').find(".dropdown-menu").removeClass("show");
                }
            });
            
            // 激活用户信息
            setTimeout(function() {
                $("#navbarUserInfo").addClass("active");
            }, 10);
            
            // 绑定方案进度按钮点击事件
            $(document).on('click', '#planProgressBtn', function(e) {
                e.preventDefault();
                e.stopPropagation();
                
                // 检查用户是否登录
                var userId = localStorage.getItem("userId");
                if (!userId) {
                    alert("请先登录");
                    // 显示登录模态框
                    $("#loginModal").addClass("active");
                    return;
                }
                
                // 显示方案进度模态框
                $("#planProgressModal").addClass("active");
                // 加载活动需求数据
                loadModalActivityDemandData();
            });
            
            // 绑定退出登录按钮点击事件
            $("#logoutBtn").click(function(e) {
                e.preventDefault();
                e.stopPropagation();
                
                if(confirm('确定要退出登录吗？')) {
                    // 清除localStorage中的用户信息
                    localStorage.removeItem("user");
                    localStorage.removeItem("userId");
                    localStorage.removeItem("roleId");
                    localStorage.removeItem("name");
                    localStorage.removeItem("sex");
                    localStorage.removeItem("post");
                    localStorage.removeItem("autoLogoutTimer");
                    
                    // 清空导航栏中的用户信息
                    $("#navbarUserInfo").removeClass("active").empty();
                    
                    // 显示登录按钮
                    $("#headerLoginBtn").show();
                    
                    // 所有职位都跳转到index.html
                    window.location.href = 'index.html';
                }
            });
        }
        
        // 加载模态框活动需求数据
        function loadModalActivityDemandData() {
            var userId = localStorage.getItem("userId");
            if (!userId) {
                alert("请先登录");
                return;
            }
            
            // 显示加载状态
            $("#modalReportTableBody").html('<tr><td colspan="9" style="text-align: center; padding: 20px;">加载中...</td></tr>');
            $("#modalNoDataMsg").hide();
            
            // 调用后端API获取活动需求数据
            var url = "http://localhost:8080/EventPlanning_Management/ShowActivityDemandByUserId";
            
            $.ajax({
                url: url,
                type: "POST",
                data: { userId: userId },
                dataType: "json",
                success: function(response) {
                    try {
                        // 尝试解析JSON响应
                        var obj = typeof response === 'string' ? JSON.parse(response) : response;
                        
                        // 检查是否是错误响应
                        if (obj.flag === 'fail' || obj.flag === 'error') {
                            // 只显示data部分的内容
                            alert(obj.data || "获取数据失败");
                            $("#modalReportTableBody").empty();
                            $("#modalNoDataMsg").show();
                            return;
                        }
                        
                        renderModalReportData(obj);
                    } catch (e) {
                        // 如果解析失败，直接处理响应
                        if (typeof response === 'string') {
                            try {
                                var parsedResponse = JSON.parse(response);
                                // 检查是否是错误响应
                                if (parsedResponse.flag === 'fail' || parsedResponse.flag === 'error') {
                                    // 只显示data部分的内容
                                    alert(parsedResponse.data || "获取数据失败");
                                    $("#modalReportTableBody").empty();
                                    $("#modalNoDataMsg").show();
                                    return;
                                }
                                renderModalReportData(parsedResponse);
                            } catch (e) {
                                alert("获取数据失败：" + response);
                                $("#modalReportTableBody").empty();
                                $("#modalNoDataMsg").show();
                            }
                        } else {
                            // 检查是否是错误响应
                            if (response.flag === 'fail' || response.flag === 'error') {
                                // 只显示data部分的内容
                                alert(response.data || "获取数据失败");
                                $("#modalReportTableBody").empty();
                                $("#modalNoDataMsg").show();
                                return;
                            }
                            alert("获取数据失败：" + JSON.stringify(response));
                            $("#modalReportTableBody").empty();
                            $("#modalNoDataMsg").show();
                        }
                    }
                },
                error: function(xhr, status, error) {
                    alert("获取数据失败，请检查网络连接或稍后重试。错误信息：" + error + "\n状态码：" + xhr.status);
                    $("#modalReportTableBody").empty();
                    $("#modalNoDataMsg").show();
                }
            });
        }
        
        // 渲染模态框报表数据
        function renderModalReportData(data) {
            var reportListBody = $("#modalReportListBody");
            var noDataMsg = $("#modalNoDataMsg");
            
            reportListBody.empty();
            
            // 检查数据是否存在
            if (!data || !data.data || data.data.length === 0) {
                noDataMsg.show();
                return;
            }
            
            noDataMsg.hide();
            
            // 遍历数据，生成活动项
            data.data.forEach(function(item) {
                // 活动ID
                var demandId = item.demandId || item.id || '未知';
                
                // 活动类型
                var activityType = item.activity_type || item.activityType || '未知';
                
                // 活动地点
                var activityPlace = item.activity_place || item.activityPlace || '未知';
                
                // 活动时间
                var startTime = item.start_time || item.startTime || '';
                var endTime = item.end_time || item.endTime || '';
                var timeText = startTime && endTime ? startTime + ' 至 ' + endTime : '未知';
                
                // 参与人数
                var participantCount = item.activity_people || item.participantCount || '未知';
                
                // 预算
                var budget = item.activity_budget || item.budget || '未知';
                
                // 需求描述
                var description = item.requirement_desc || item.demandDescription || item.description || '无';
                
                // 状态
                var statusText = '未知';
                var statusClass = 'status-pending';
                var activityProgress = item.activity_progress || item.status || 0;
                switch (activityProgress) {
                    case 0:
                    case '0':
                        statusText = '待处理';
                        statusClass = 'status-pending';
                        break;
                    case 1:
                    case '1':
                        statusText = '业务管理员审批中';
                        statusClass = 'status-processing';
                        break;
                    case 2:
                    case '2':
                        statusText = '仓库管理员审批中';
                        statusClass = 'status-processing';
                        break;
                    case 3:
                    case '3':
                        statusText = '人员安排完成';
                        statusClass = 'status-approved';
                        break;
                    case -1:
                    case '-1':
                        statusText = '业务管理员拒绝';
                        statusClass = 'status-rejected';
                        break;
                    case -2:
                    case '-2':
                        statusText = '仓库管理员拒绝';
                        statusClass = 'status-rejected';
                        break;
                    case -3:
                    case '-3':
                        statusText = '用户已取消';
                        statusClass = 'status-rejected';
                        break;
                    default:
                        statusText = '未知状态';
                        statusClass = 'status-pending';
                }
                
                // 提交时间
                var submitTime = item.create_time || item.submitTime || '未知';
                
                // 创建活动项
                var activityItem = $('<div class="activity-item"></div>');
                
                // 活动项头部
                var header = $('<div class="activity-item-header"></div>');
                var title = $('<div class="activity-item-title">活动 #' + demandId + '</div>');
                var status = $('<div class="activity-item-status ' + statusClass + '">' + statusText + '</div>');
                header.append(title, status);
                
                // 活动项内容
                var content = $('<div class="activity-item-content"></div>');
                var typeInfo = $('<div class="activity-item-info"><span class="activity-item-info-label">活动类型:</span><span class="activity-item-info-value">' + activityType + '</span></div>');
                var placeInfo = $('<div class="activity-item-info"><span class="activity-item-info-label">活动地点:</span><span class="activity-item-info-value">' + activityPlace + '</span></div>');
                var timeInfo = $('<div class="activity-item-info"><span class="activity-item-info-label">活动时间:</span><span class="activity-item-info-value">' + timeText + '</span></div>');
                var peopleInfo = $('<div class="activity-item-info"><span class="activity-item-info-label">参与人数:</span><span class="activity-item-info-value">' + participantCount + '</span></div>');
                var budgetInfo = $('<div class="activity-item-info"><span class="activity-item-info-label">预算:</span><span class="activity-item-info-value">' + budget + '</span></div>');
                content.append(typeInfo, placeInfo, timeInfo, peopleInfo, budgetInfo);
                
                // 活动项描述
                var desc = $('<div class="activity-item-description"><span class="activity-item-info-label">需求描述:</span><span class="activity-item-info-value">' + description + '</span></div>');
                
                // 活动项底部
                var footer = $('<div class="activity-item-footer"></div>');
                var time = $('<div class="activity-item-time">提交时间: ' + submitTime + '</div>');
                var action = $('<button class="activity-item-action" onclick="showActivityDetail(' + JSON.stringify(item) + ')">查看详情</button>');
                footer.append(time, action);
                
                // 组装活动项
                activityItem.append(header, content, desc, footer);
                
                // 添加到列表
                reportListBody.append(activityItem);
            });
        }
        
        // 显示活动详情
        function showActivityDetail(item) {
            // 填充详情数据
            $("#detailDemandId").text(item.demandId || item.id || '未知');
            $("#detailActivityType").text(item.activity_type || item.activityType || '未知');
            $("#detailActivityPlace").text(item.activity_place || item.activityPlace || '未知');
            
            var startTime = item.start_time || item.startTime || '';
            var endTime = item.end_time || item.endTime || '';
            var timeText = startTime && endTime ? startTime + ' 至 ' + endTime : '未知';
            $("#detailActivityTime").text(timeText);
            
            $("#detailParticipantCount").text(item.activity_people || item.participantCount || '未知');
            $("#detailBudget").text(item.activity_budget || item.budget || '未知');
            $("#detailDescription").text(item.requirement_desc || item.demandDescription || item.description || '无');
            
            var statusText = '未知';
            var activityProgress = item.activity_progress || item.status || 0;
            switch (activityProgress) {
                case 0:
                case '0':
                    statusText = '待处理';
                    break;
                case 1:
                case '1':
                    statusText = '业务管理员审批中';
                    break;
                case 2:
                case '2':
                    statusText = '仓库管理员审批中';
                    break;
                case 3:
                case '3':
                    statusText = '人员安排完成';
                    break;
                case -1:
                case '-1':
                    statusText = '业务管理员拒绝';
                    break;
                case -2:
                case '-2':
                    statusText = '仓库管理员拒绝';
                    break;
                case -3:
                case '-3':
                    statusText = '用户已取消';
                    break;
                default:
                    statusText = '未知状态';
            }
            $("#detailStatus").text(statusText);
            
            $("#detailSubmitTime").text(item.create_time || item.submitTime || '未知');
            
            // 显示详情模态框
            $("#activityDetailModal").addClass("active");
        }
        
        // 活动详情模态框关闭按钮点击事件
        $("#activityDetailClose").on('click', function() {
            $("#activityDetailModal").removeClass("active");
        });
        
        // 活动详情模态框取消按钮点击事件
        $("#activityDetailCancelBtn").on('click', function() {
            $("#activityDetailModal").removeClass("active");
        });
        
        // 点击活动详情模态框外部关闭模态框
        $("#activityDetailModal").on('click', function(e) {
            if (e.target === this) {
                $(this).removeClass("active");
            }
        });
        
        // 页面加载时初始化
        initEmployeeMenu();
        
        // 监听职位变化（例如在个人信息修改后）
        window.updateEmployeeMenu = function() {
            initEmployeeMenu();
        };
    } else {
        // 在用户版页面上，只更新用户信息显示，不修改导航栏结构
        // 更新导航栏用户信息
        function updateNavbarUserInfo() {
            // 检查用户是否已经登录
            var user = JSON.parse(localStorage.getItem("user")) || {};
            var name = localStorage.getItem("name") || user.name || user.username;
            
            // 如果用户没有登录，不显示用户信息，而是显示登录按钮
            if (!name) {
                // 清空导航栏中的用户信息
                $("#navbarUserInfo").removeClass("active").empty();
                
                // 显示登录按钮
                $("#headerLoginBtn").show();
                
                return;
            }
            
            var avatarSrc = user.avatar || "../IMG/默认头像webp.webp";
            
            // 隐藏登录按钮
            $("#headerLoginBtn").hide();
            
            // 显示用户信息
            $("#navbarUserInfo").html(
                '<div class="user-info">' +
                    '<img class="user-avatar" src="' + avatarSrc + '" alt="用户头像">' +
                    '<span class="user-name">' + name + '</span>' +
                    '<div class="dropdown-menu">' +
                        '<button class="dropdown-item" id="changeInfoBtn">修改信息</button>' +
                        '<button class="dropdown-item" id="changePasswordBtn">修改密码</button>' +
                        '<button class="dropdown-item" id="planProgressBtn">方案进度</button>' +
                        '<div class="dropdown-divider"></div>' +
                        '<button class="dropdown-item" id="logoutBtn">退出登录</button>' +
                    '</div>' +
                '</div>'
            );
            
            // 激活用户信息
            setTimeout(function() {
                $("#navbarUserInfo").addClass("active");
            }, 10);
            
            // 绑定方案进度按钮点击事件
            $("#planProgressBtn").click(function(e) {
                e.preventDefault();
                e.stopPropagation();
                
                // 检查用户是否登录
                var userId = localStorage.getItem("userId");
                if (!userId) {
                    alert("请先登录");
                    // 显示登录模态框
                    $("#loginModal").addClass("active");
                    return;
                }
                
                // 显示方案进度模态框
                $("#planProgressModal").addClass("active");
                // 加载活动需求数据
                loadModalActivityDemandData();
            });
            
            // 绑定退出登录按钮点击事件
            $("#logoutBtn").click(function(e) {
                e.preventDefault();
                e.stopPropagation();
                
                if(confirm('确定要退出登录吗？')) {
                    // 清除localStorage中的用户信息
                    localStorage.removeItem("user");
                    localStorage.removeItem("userId");
                    localStorage.removeItem("roleId");
                    localStorage.removeItem("name");
                    localStorage.removeItem("sex");
                    localStorage.removeItem("post");
                    localStorage.removeItem("autoLogoutTimer");
                    
                    // 清空导航栏中的用户信息
                    $("#navbarUserInfo").removeClass("active").empty();
                    
                    // 显示登录按钮
                    $("#headerLoginBtn").show();
                    
                    // 所有职位都跳转到index.html
                    window.location.href = 'index.html';
                }
            });
        }
        
        // 页面加载时更新用户信息
        updateNavbarUserInfo();
    }
});
