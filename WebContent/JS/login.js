$(function() {
    // 全局变量：跟踪登录状态
    var isLoggedIn = false;
    
    // 检查用户是否已登录
    checkLoginStatus();
    
    // 绑定悬停事件处理 - 绑定到整个用户信息容器
    var hoverTimer;
    $("#navbarUserInfo").hover(
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
    
    // 使用事件委托绑定修改密码按钮点击事件
    $(document).on('click', '#changePasswordBtn', function(e) {
        e.preventDefault();
        e.stopPropagation();
        $("#changePasswordModal").addClass("active");
        $("#changePasswordForm")[0].reset();
        $("#changePasswordError").hide();
    });
    
    // 切换版本按钮点击事件
    $("#switchVersionBtn").click(function() {
        var roleId = localStorage.getItem("roleId");
        if(!roleId) {
            alert("请先登录");
            return;
        }
        
        // 检查当前页面是否已经是员工相关页面
        var currentUrl = window.location.pathname;
        if(currentUrl.includes("employee.html") || currentUrl.includes("salary.html") || 
           currentUrl.includes("employee_list.html") || currentUrl.includes("approval.html")) {
            alert("您已经是员工版，无需切换");
            return;
        }
        
        // 只有roleId为1的员工才能跳转到员工版
        if(roleId == "1") {
            // 员工角色直接跳转到员工主页
            $("body").addClass("fade-out");
            setTimeout(function() {
                window.location.href = "employee.html";
            }, 300);
        } else {
            // 非员工角色不能切换到员工版
            alert("只有员工才能切换到员工版");
        }
    });
    
    // 切换到用户版按钮点击事件
    $("#switchToUserBtn").click(function() {
        // 检查当前页面是否已经是用户版页面
        var currentUrl = window.location.pathname;
        if(currentUrl.includes("index.html") || currentUrl.includes("about.html") || 
           currentUrl.includes("contact.html") || currentUrl.includes("plan.html") || 
           currentUrl.includes("service.html")) {
            // 已经是用户版，直接跳转到首页
            window.location.href = "index.html";
            return;
        }
        
        // 直接切换到用户版
        $("body").addClass("fade-out");
        setTimeout(function() {
            window.location.href = "index.html";
        }, 300);
    });
    

    
    // 登录按钮点击事件
    $("#loginBtn").click(function() {
        var username = $("#modalUsername").val();
        var password = $("#modalPassword").val();
        var role = $("#loginRole").val();
        
        if(!username || !password || !role) {
            alert("请填写所有必填项");
            return;
        }
        
        // 根据角色确定roleId：1为员工，2为用户
        var roleId = 2;
        if(role == "user") {
            roleId = 2;
        } else if(role == "employee") {
            roleId = 1;
        }
        
        // POST请求参数
        var params = {
            username: username,
            password: password,
            roleId: roleId
        };
        
        $.ajax({
            url: "http://localhost:8080/EventPlanning_Management/login",
            type: "POST",
            data: params,
            success: function(msg) {
                var obj = JSON.parse(msg);
                if(obj.flag == "success") {
                    // 检查账号状态
                    if(obj.data && obj.data.status === 0) {
                        // 账号被封禁，不关闭登录表单
                        alert("此账号已被封禁、请找管理员解封");
                        return;
                    }
                    
                    // 保存用户信息到localStorage
                    localStorage.setItem("user", JSON.stringify(obj.data));
                    
                    // 分别保存关键字段，确保即使obj.data缺少某些字段也能正常工作
                    localStorage.setItem("userId", obj.data.userId || obj.data.id || "");
                    localStorage.setItem("roleId", roleId);
                    localStorage.setItem("name", obj.data.name || obj.data.username || "用户");
                    localStorage.setItem("sex", obj.data.sex || "");
                    localStorage.setItem("post", obj.data.post || "");
                    // 更新登录状态
                    isLoggedIn = true;
                    
                    // 弹出登录成功提示
                    alert("登录成功！");
                    
                    // 关闭登录模态框
                    $("#loginModal").removeClass("active");
                    
                    // 更新导航栏中的用户信息
                    updateNavbarUserInfo(obj.data);
                    
                    // 启动1小时自动退出定时器
                    var autoLogoutTimer = setTimeout(function() {
                        autoLogout();
                    }, 60 * 60 * 1000);
                    
                    // 保存定时器ID以便清除
                    localStorage.setItem("autoLogoutTimer", autoLogoutTimer);
                    
                    // 刷新页面，确保用户信息正确显示
                    window.location.reload();
                    
                } else {
                    // 登录失败，不关闭登录表单
                    alert(obj.data);
                }
            },
            error: function() {
                // 登录失败，不关闭登录表单
                alert("登录失败，请检查网络连接");
            }
        })
    });
    
    // 注册按钮点击事件
    $("#registerBtn").click(function() {
        showRegisterForm();
    });
    
    // 返回登录按钮点击事件
    $("#backToLoginBtn").click(function() {
        showLoginForm();
    });
    
    // 角色选择变化事件，用于动态显示/隐藏职位字段和城市字段
    $("#registerRole").change(function() {
        var role = $(this).val();
        var postContainer = $("#registerPostContainer");
        var cityContainer = $("#registerCityContainer");
        if (role === "employee") {
            postContainer.show();
            cityContainer.show();
        } else {
            postContainer.hide();
            cityContainer.hide();
        }
    });
    
    // 提交注册按钮点击事件
    $("#submitRegisterBtn").click(function() {
        var username = $("#registerUsername").val();
        var role = $("#registerRole").val();
        var password = $("#registerPassword").val();
        var confirmPassword = $("#registerConfirmPassword").val();
        var name = $("#registerName").val();
        var phone = $("#registerPhone").val();
        var sex = $("#registerSex").val();
        
        // 验证必填项
        if(!username || !role || !password || !confirmPassword) {
            alert("请填写所有必填项");
            return;
        }
        
        // 验证密码一致性
        if(password !== confirmPassword) {
            alert("两次输入的密码不一致");
            return;
        }
        
        // 如果是员工角色，验证姓名、电话、性别、城市和职位为必填项
        if(role == "employee") {
            var post = $("#registerPost").val();
            var city = $("#registerCity").val();
            if(!name || !phone || !sex || !city || !post) {
                alert("员工角色需要填写姓名、电话、性别、城市和职位");
                return;
            }
        }
        
        // 根据角色确定roleId：1为员工，2为用户
        var roleId = 2;
        if(role == "user") {
            roleId = 2;
        } else if(role == "employee") {
            roleId = 1;
        }
        
        // 根据角色构建不同的请求数据和URL
        var url;
        var requestData;
        if(role == "user") {
            // 用户角色使用原来的API
            url = "http://localhost:8080/EventPlanning_Management/register";
            requestData = {
                username: username,
                password: password,
                roleId: roleId
            };
        } else if(role == "employee") {
            // 员工角色使用新的API
            var post = $("#registerPost").val();
            var city = $("#registerCity").val();
            console.log('员工注册，职位:', post, '城市:', city);
            url = "http://localhost:8080/EventPlanning_Management/EmployeeRegister";
            requestData = {
                username: username,
                password: password,
                employeeName: name,
                phone: phone,
                sex: sex,
                post: post,
                city: city
            };
        }
        
        // 保存roleId到局部变量，确保在AJAX回调中可用
        var registerRoleId = roleId;
        
        $.ajax({
            url: url,
            type: "POST",
            data: requestData,
            success: function(msg) {
                // 调试：输出完整返回数据
                console.log("注册API返回数据:", msg);
                console.log("原始msg类型:", typeof msg);
                
                var obj = typeof msg === 'string' ? JSON.parse(msg) : msg;
                
                // 调试：输出解析后的obj和flag值
                console.log("解析后的obj:", obj);
                console.log("obj.flag值:", obj.flag);
                console.log("obj.flag类型:", typeof obj.flag);
                
                // 优化条件判断，使用更宽松的比较
                var isSuccess = false;
                if(obj.flag) {
                    // 将flag转换为小写进行比较，避免大小写问题
                    var flagLower = String(obj.flag).toLowerCase();
                    isSuccess = flagLower === "success" || flagLower === "ok" || flagLower === "true";
                } else if(obj.data && (String(obj.data).toLowerCase().includes("成功") || String(obj.data).toLowerCase().includes("success"))) {
                    // 处理一些API可能直接返回data而没有flag的情况
                    isSuccess = true;
                }
                
                console.log("isSuccess:", isSuccess);
                console.log("registerRoleId:", registerRoleId);
                
                if(isSuccess) {
                    // 先alert，后切换表单
                    var alertMessage = "";
                    if(registerRoleId == 2) {
                        // 普通用户直接注册成功
                        alertMessage = "注册成功！";
                    } else if(registerRoleId == 1) {
                        // 员工注册需要等待管理员审核
                        alertMessage = "注册成功，等待管理员审核！";
                    }
                    
                    console.log("准备显示alert:", alertMessage);
                    alert(alertMessage);
                    
                    // 延迟切换表单，确保alert能正常显示
                    setTimeout(function() {
                        showLoginForm();
                    }, 100);
                } else {
                    // 调试：输出失败情况
                    console.log("注册失败，obj.flag值不符合条件");
                    // 注册失败，保持在注册表单
                    alert(obj.data || "注册失败");
                }
            },
            error: function(xhr, status, error) {
                // 调试：输出错误信息
                console.log("AJAX错误:", status, error);
                console.log("xhr对象:", xhr);
                // 注册失败，保持在注册表单
                alert("注册失败，请检查网络连接。错误信息：" + error);
            }
        })
    });
    
    // 自动退出登录函数
    function autoLogout() {
        // 清除localStorage中的用户信息
        localStorage.removeItem("user");
        localStorage.removeItem("userId");
        localStorage.removeItem("roleId");
        localStorage.removeItem("name");
        localStorage.removeItem("sex");
        localStorage.removeItem("post");
        localStorage.removeItem("autoLogoutTimer");
        
        // 更新登录状态
        isLoggedIn = false;
        
        // 清除导航栏中的用户信息
        clearNavbarUserInfo();
        
        // 显示登录按钮
        $("#headerLoginBtn").show();
        
        alert("登录已过期，请重新登录");
    }
    
    // 修改密码按钮点击事件
    $("#changePasswordBtn").click(function(e) {
        e.preventDefault();
        e.stopPropagation();
        $("#changePasswordModal").addClass("active");
        $("#changePasswordForm")[0].reset();
        $("#changePasswordError").hide();
    });
    
    // 提交修改密码按钮点击事件
    // 使用事件委托绑定提交修改密码按钮点击事件
    $(document).on('click', '#submitChangePasswordBtn', function(e) {
        e.preventDefault();
        e.stopPropagation();
        
        var oldPassword = $("#oldPassword").val();
        var newPassword = $("#newPassword").val();
        var confirmNewPassword = $("#confirmNewPassword").val();
        
        if(!oldPassword || !newPassword || !confirmNewPassword) {
            alert("请填写所有必填项");
            return;
        }
        
        if(newPassword !== confirmNewPassword) {
            $("#changePasswordError").text("两次输入的新密码不一致");
            $("#changePasswordError").show();
            return;
        }
        
        if(newPassword.length < 6) {
            $("#changePasswordError").text("新密码长度不能少于6位");
            $("#changePasswordError").show();
            return;
        }
        
        var userId = localStorage.getItem("userId");
        if(!userId) {
            alert("用户未登录，请重新登录");
            return;
        }
        
        var params = "userId=" + userId + "&password=" + oldPassword + "&newpassword=" + newPassword;
        
        $.ajax({
            url: "http://localhost:8080/EventPlanning_Management/changePassword",
            type: "post",
            contentType: "application/x-www-form-urlencoded",
            data: params,
            success: function(msg) {
                try {
                    var obj = typeof msg === 'string' ? JSON.parse(msg) : msg;
                    if(obj.flag == "success") {
                        alert("密码修改成功！");
                        $("#changePasswordModal").removeClass("active");
                        $("#changePasswordForm")[0].reset();
                        $("#changePasswordError").hide();
                    } else {
                        $("#changePasswordError").text(obj.data || "修改密码失败");
                        $("#changePasswordError").show();
                    }
                } catch (e) {
                    alert("响应解析失败：" + e.message);
                }
            },
            error: function(xhr, status, error) {
                alert("密码修改失败，请检查网络连接。错误信息：" + error);
            }
        });
    });
    
    // 使用事件委托绑定取消修改密码按钮点击事件
    $(document).on('click', '#cancelChangePasswordBtn', function(e) {
        e.preventDefault();
        e.stopPropagation();
        $("#changePasswordModal").removeClass("active");
        $("#changePasswordForm")[0].reset();
        $("#changePasswordError").hide();
    });
    
    // 使用事件委托关闭修改密码模态框
    $(document).on('click', '#changePasswordClose', function(e) {
        e.preventDefault();
        e.stopPropagation();
        $("#changePasswordModal").removeClass("active");
        $("#changePasswordForm")[0].reset();
        $("#changePasswordError").hide();
    });
    
    // 修改信息按钮点击事件
    $("#changeInfoBtn").click(function() {
        var user = JSON.parse(localStorage.getItem("user"));
        if(user) {
            $("#changeName").val(user.name);
            $("#changeSex").val(user.sex);
            $("#currentAvatar").attr("src", user.avatar || "../IMG/默认头像webp.webp");
        }
        $("#changeInfoModal").addClass("active");
    });
    
    // 头像文件选择预览
    $("#avatarFile").change(function() {
        var file = this.files[0];
        if(file) {
            var reader = new FileReader();
            reader.onload = function(e) {
                $("#currentAvatar").attr("src", e.target.result);
            };
            reader.readAsDataURL(file);
        }
    });
    
    // 提交修改信息按钮点击事件
    $("#submitChangeInfoBtn").click(function() {
        var name = $("#changeName").val();
        var sex = $("#changeSex").val();
        var phone = $("#changePhone").val();
        var avatarFile = $("#avatarFile")[0].files[0];
        
        if(!name || !sex || !phone) {
            alert("请填写所有必填项");
            return;
        }
        
        var userId = localStorage.getItem("userId");
        var formData = new FormData();
        formData.append("userId", userId);
        formData.append("name", name);
        formData.append("sex", sex);
        formData.append("phone", phone);
        if(avatarFile) {
            formData.append("avatar", avatarFile);
        }
        
        $.ajax({
            url: "http://localhost:8080/EventPlanning_Management/updateUserInfo",
            type: "post",
            data: formData,
            processData: false,
            contentType: false,
            success: function(msg) {
                var obj = JSON.parse(msg);
                if(obj.flag == "success") {
                    alert("信息修改成功！");
                    
                    var userData = JSON.parse(localStorage.getItem("user"));
                    userData.name = name;
                    userData.sex = sex;
                    if(obj.data && obj.data.avatar) {
                        userData.avatar = obj.data.avatar;
                    }
                    localStorage.setItem("user", JSON.stringify(userData));
                    
                    updateNavbarUserInfo(userData);
                    
                    $("#changeInfoModal").removeClass("active");
                    $("#changeInfoForm")[0].reset();
                } else {
                    alert(obj.data);
                }
            },
            error: function() {
                alert("信息修改失败，请检查网络连接");
            }
        });
    });
    
    // 取消修改信息按钮点击事件
    $("#cancelChangeInfoBtn").click(function() {
        $("#changeInfoModal").removeClass("active");
        $("#changeInfoForm")[0].reset();
    });
    
    // 关闭修改信息模态框
    $("#changeInfoClose").click(function() {
        $("#changeInfoModal").removeClass("active");
        $("#changeInfoForm")[0].reset();
    });
    
    // 关闭登录模态框显示登录表单
    function showLoginForm() {
        // 添加退出动画到注册表单
        if ($("#registerFormContainer").is(":visible")) {
            $("#registerFormContainer").addClass("exiting");
            
            // 等待退出动画结束后显示登录表单
            setTimeout(() => {
                // 移除动画类
                $("#registerFormContainer").removeClass("exiting");
                
                // 更新UI
                $("#modalTitle").text("活动策划管理平台");
                $("#registerFormContainer").hide();
                $("#loginFormContainer").show();
                $("#loginBtn").show();
                $("#registerBtn").show();
                $("#submitRegisterBtn").hide();
                $("#backToLoginBtn").hide();
                
                // 添加进入动画
                $("#loginFormContainer").addClass("active");
                
                // 重置表单
                $("#modalLoginForm")[0].reset();
                
                // 移除进入动画类
                setTimeout(() => {
                    $("#loginFormContainer").removeClass("active");
                }, 400);
                
                // 聚焦到用户名输入框
                setTimeout(() => {
                    $("#modalUsername").focus();
                }, 200);
            }, 400);
        } else {
            // 初始状态，直接显示登录表单
            $("#modalTitle").text("活动策划管理平台");
            $("#loginFormContainer").show();
            $("#registerFormContainer").hide();
            $("#loginBtn").show();
            $("#registerBtn").show();
            $("#submitRegisterBtn").hide();
            $("#backToLoginBtn").hide();
            // 重置表单
            $("#modalLoginForm")[0].reset();
            // 聚焦到用户名输入框
            setTimeout(() => {
                $("#modalUsername").focus();
            }, 200);
        }
    }
    
    // 显示注册表单
    function showRegisterForm() {
        // 添加退出动画到登录表单
        if ($("#loginFormContainer").is(":visible")) {
            $("#loginFormContainer").addClass("exiting");
            
            // 等待退出动画结束后显示注册表单
            setTimeout(() => {
                // 移除动画类
                $("#loginFormContainer").removeClass("exiting");
                
                // 更新UI
                $("#modalTitle").text("注册 - 活动策划管理平台");
                $("#loginFormContainer").hide();
                $("#registerFormContainer").show();
                $("#loginBtn").hide();
                $("#registerBtn").hide();
                $("#submitRegisterBtn").show();
                $("#backToLoginBtn").show();
                
                // 添加进入动画
                $("#registerFormContainer").addClass("active");
                
                // 重置表单
                $("#modalRegisterForm")[0].reset();
                
                // 移除进入动画类
                setTimeout(() => {
                    $("#registerFormContainer").removeClass("active");
                }, 400);
                
                // 聚焦到用户名输入框
                setTimeout(() => {
                    $("#registerUsername").focus();
                }, 200);
            }, 400);
        } else {
            // 初始状态，直接显示注册表单
            $("#modalTitle").text("注册 - 活动策划管理平台");
            $("#loginFormContainer").hide();
            $("#registerFormContainer").show();
            $("#loginBtn").hide();
            $("#registerBtn").hide();
            $("#submitRegisterBtn").show();
            $("#backToLoginBtn").show();
            // 重置表单
            $("#modalRegisterForm")[0].reset();
            // 聚焦到用户名输入框
            setTimeout(() => {
                $("#registerUsername").focus();
            }, 200);
        }
    }
    
    // 检查登录状态并更新显示
    function checkLoginStatus() {
        var user = localStorage.getItem("user");
        var userId = localStorage.getItem("userId");
        var name = localStorage.getItem("name");
        var roleId = localStorage.getItem("roleId");
        var post = localStorage.getItem("post");
        
        // 检查是否有有效的登录信息
        if(user || userId || name || roleId) {
            try {
                var userData = user ? JSON.parse(user) : {};
                
                // 确保userData对象存在
                if(!userData) {
                    userData = {};
                }
                
                // 使用localStorage中的单独字段补充userData
                if(userId && !userData.userId) {
                    userData.userId = userId;
                }
                if(name && !userData.name) {
                    userData.name = name;
                }
                if(roleId && !userData.roleId) {
                    userData.roleId = roleId;
                }
                
                // 从localStorage获取sex和post字段
                var sex = localStorage.getItem("sex");
                if(sex && !userData.sex) {
                    userData.sex = sex;
                }
                if(post && !userData.post) {
                    userData.post = post;
                }
                
                // 如果userData中没有name，使用username或其他字段代替
                if(!userData.name) {
                    userData.name = userData.username || "用户";
                }
                
                // 确保userId存在
                if(!userData.userId) {
                    userData.userId = userId || "unknown";
                }
                
                isLoggedIn = true;
                updateNavbarUserInfo(userData);
                
                // 清除之前的自动退出定时器
                var autoLogoutTimer = localStorage.getItem("autoLogoutTimer");
                if(autoLogoutTimer) {
                    clearTimeout(autoLogoutTimer);
                    localStorage.removeItem("autoLogoutTimer");
                }
            } catch(e) {
                // JSON解析错误，保留localStorage数据并尝试使用
                console.error("JSON解析错误:", e);
                // 创建一个基本的用户数据对象
                var basicUserData = {
                    userId: userId || "unknown",
                    name: name || "用户",
                    roleId: roleId || "2",
                    post: post || ""
                };
                isLoggedIn = true;
                updateNavbarUserInfo(basicUserData);
            }
        } else {
            // 未登录状态，显示登录按钮
            isLoggedIn = false;
            $("#headerLoginBtn").show();
            $("#navbarUserInfo").removeClass("active");
        }
    }
    
    // 更新导航栏中的用户信息
    function updateNavbarUserInfo(user) {
        if(user && user.name) {
            // 隐藏登录按钮
            $("#headerLoginBtn").hide();
            
            // 获取头像路径
            var avatarSrc = user.avatar || "../IMG/默认头像webp.webp";
            
            // 显示用户信息
            $("#navbarUserInfo").html(
                '<div class="user-info" id="userInfoDropdown">' +
                    '<img class="user-avatar" src="' + avatarSrc + '" alt="用户头像">' +
                    '<span class="user-name">' + user.name + '</span>' +
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
    
    // 使用事件委托绑定修改信息按钮点击事件
    $(document).on('click', '#changeInfoBtn', function(e) {
        e.preventDefault();
        e.stopPropagation();
        var userData = JSON.parse(localStorage.getItem("user"));
        if(userData) {
            $("#changeName").val(userData.name);
            $("#changeSex").val(userData.sex);
            $("#currentAvatar").attr("src", userData.avatar || "../IMG/默认头像webp.webp");
        }
        $("#changeInfoModal").addClass("active");
    });
    
    // 使用事件委托绑定方案进度按钮点击事件
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
            
            // 使用事件委托绑定退出登录按钮点击事件
            $(document).on('click', '#logoutBtn', function(e) {
                e.preventDefault();
                e.stopPropagation();
                
                // 显示确认提示
                if(confirm("确定要退出登录吗？")) {
                    // 清除localStorage中的用户信息
                    localStorage.removeItem("user");
                    localStorage.removeItem("userId");
                    localStorage.removeItem("roleId");
                    localStorage.removeItem("name");
                    localStorage.removeItem("sex");
                    localStorage.removeItem("post");
                    localStorage.removeItem("autoLogoutTimer");
                    
                    // 更新登录状态
                    isLoggedIn = false;
                    
                    // 清除导航栏中的用户信息
                    clearNavbarUserInfo();
                    
                    // 显示登录按钮
                    $("#headerLoginBtn").show();
                    
                    // 检查当前页面是否是员工版页面
                    var currentUrl = window.location.pathname;
                    var isEmployeePage = currentUrl.includes("employee.html") || 
                                         currentUrl.includes("salary.html") || 
                                         currentUrl.includes("employee_list.html") || 
                                         currentUrl.includes("approval.html") || 
                                         currentUrl.includes("manage_activity_applications.html") || 
                                         currentUrl.includes("user_contact.html") || 
                                         currentUrl.includes("staff_arrangement.html") || 
                                         currentUrl.includes("update_case_library.html");
                    
                    if(isEmployeePage) {
                        // 如果是员工版页面，跳转到主界面
                        window.location.href = "index.html";
                    } else {
                        // 否则刷新当前页面
                        window.location.reload();
                    }
                }
            });
        }
    }
    
    // 清除导航栏中的用户信息
    function clearNavbarUserInfo() {
        $("#navbarUserInfo").removeClass("active");
        setTimeout(function() {
            $("#navbarUserInfo").html("");
        }, 300);
    }
    
    // 登录按钮点击事件
    $("#headerLoginBtn").click(function() {
        // 显示登录模态框
        $("#loginModal").addClass("active");
        showLoginForm();
    });
    
    // 关闭登录模态框
    $("#modalClose").click(function() {
        $("#loginModal").removeClass("active");
        showLoginForm();
    });
    
    // 登录模态框外部点击不关闭，仅允许点击叉叉关闭
    
    // 修改密码模态框外部点击不关闭，仅允许点击叉叉关闭
    
    // 修改信息模态框外部点击不关闭，仅允许点击叉叉关闭
    
    // 点击用户信息容器内部元素阻止事件冒泡
    $("#navbarUserInfo").find(".user-info").click(function(e) {
        e.stopPropagation();
    });
    
    // 点击下拉菜单项阻止事件冒泡
    $("#navbarUserInfo").find(".dropdown-item").click(function(e) {
        e.stopPropagation();
    });
});
