// 轮播图功能实现
document.addEventListener('DOMContentLoaded', function() {
    // 获取轮播图相关元素
    const carousel = document.querySelector('.carousel');
    const carouselItems = document.querySelectorAll('.carousel-item');
    const prevBtn = document.querySelector('.carousel-control.prev');
    const nextBtn = document.querySelector('.carousel-control.next');
    const indicators = document.querySelectorAll('.indicator');
    
    let currentIndex = 0;
    const totalItems = carouselItems.length;
    const slideInterval = 5000; // 自动轮播间隔时间（毫秒）
    let intervalId;
    let isTransitioning = false;
    
    // 初始化轮播图
    function initCarousel() {
        showSlide(currentIndex);
        startAutoSlide();
        
        // 添加事件监听
        prevBtn.addEventListener('click', () => {
            if (!isTransitioning) {
                prevSlide();
                resetAutoSlide();
            }
        });
        
        nextBtn.addEventListener('click', () => {
            if (!isTransitioning) {
                nextSlide();
                resetAutoSlide();
            }
        });
        
        // 指示器点击事件
        indicators.forEach((indicator, index) => {
            indicator.addEventListener('click', () => {
                if (!isTransitioning && index !== currentIndex) {
                    goToSlide(index);
                    resetAutoSlide();
                }
            });
        });
        
        // 鼠标悬停时停止自动轮播，离开时继续
        carousel.addEventListener('mouseenter', stopAutoSlide);
        carousel.addEventListener('mouseleave', startAutoSlide);
    }
    
    // 显示指定索引的幻灯片
    function showSlide(index) {
        isTransitioning = true;
        
        // 隐藏所有幻灯片
        carouselItems.forEach(item => {
            item.classList.remove('active');
        });
        
        // 移除所有指示器的激活状态
        indicators.forEach(indicator => {
            indicator.classList.remove('active');
        });
        
        // 显示当前幻灯片
        carouselItems[index].classList.add('active');
        
        // 激活当前指示器
        indicators[index].classList.add('active');
        
        // 重置过渡状态
        setTimeout(() => {
            isTransitioning = false;
        }, 800); // 与CSS过渡时间匹配
    }
    
    // 切换到上一张幻灯片
    function prevSlide() {
        currentIndex = (currentIndex - 1 + totalItems) % totalItems;
        showSlide(currentIndex);
    }
    
    // 切换到下一张幻灯片
    function nextSlide() {
        currentIndex = (currentIndex + 1) % totalItems;
        showSlide(currentIndex);
    }
    
    // 跳转到指定索引的幻灯片
    function goToSlide(index) {
        currentIndex = index;
        showSlide(currentIndex);
    }
    
    // 开始自动轮播
    function startAutoSlide() {
        // 确保只设置一个计时器
        if (intervalId) {
            clearInterval(intervalId);
        }
        intervalId = setInterval(nextSlide, slideInterval);
    }
    
    // 停止自动轮播
    function stopAutoSlide() {
        clearInterval(intervalId);
        intervalId = null;
    }
    
    // 重置自动轮播计时器
    function resetAutoSlide() {
        stopAutoSlide();
        startAutoSlide();
    }
    
    // 服务项目标签交互
    const serviceTags = document.querySelectorAll('.service-tags span');
    serviceTags.forEach(tag => {
        tag.addEventListener('click', function() {
            // 这里可以添加标签点击后的交互逻辑
            console.log('点击了服务标签:', this.textContent);
        });
    });
    
    // 城市选择器交互
const citySelector = document.querySelector('.city-selector');
const cityCurrent = document.querySelector('.city-current');
const cityDropdown = document.querySelector('.city-dropdown');
const cityItems = document.querySelectorAll('.city-item');

// 点击城市选择器显示/隐藏下拉框
cityCurrent.addEventListener('click', function(e) {
    e.stopPropagation();
    cityDropdown.classList.toggle('active');
});

// 点击城市选项更新显示的城市
cityItems.forEach(item => {
    item.addEventListener('click', function(e) {
        e.stopPropagation();
        const cityName = this.textContent;
        cityCurrent.innerHTML = cityName + ' <span class="arrow">↓</span>';
        cityDropdown.classList.remove('active');
        console.log('选择了城市:', cityName);
    });
});

// 点击页面其他地方关闭下拉框
document.addEventListener('click', function() {
    cityDropdown.classList.remove('active');
});
    
    // 导航菜单交互
    const navLinks = document.querySelectorAll('.nav-menu a');
    navLinks.forEach(link => {
        link.addEventListener('click', function(e) {
            // 只阻止没有实际链接的导航项（href="#"）
            if (this.getAttribute('href') === '#') {
                e.preventDefault();
                // 这里可以添加导航链接点击后的滚动或跳转逻辑
                console.log('点击了导航链接:', this.textContent);
            }
        });
    });
    
    // 方案进度模态框事件处理
    // 方案进度按钮点击事件 (通过事件委托绑定)
    $(document).on('click', '#planProgressBtn', function(e) {
        e.preventDefault();
        e.stopPropagation();
        
        // 检查用户是否登录
        var userId = localStorage.getItem("userId");
        if (!userId) {
            alert("请先登录");
            // 显示登录模态框
            $('#loginModal').addClass('active');
            return;
        }
        
        // 显示方案进度模态框
        $('#planProgressModal').addClass('active');
        // 加载活动需求数据
        loadModalActivityDemandData();
    });
    
    // 模态框关闭按钮点击事件
    $('#planProgressClose').on('click', function() {
        $('#planProgressModal').removeClass('active');
    });
    
    // 模态框取消按钮点击事件
    $('#planProgressCancelBtn').on('click', function() {
        $('#planProgressModal').removeClass('active');
    });
    
    // 模态框刷新报表按钮点击事件
    $('#modalRefreshReportBtn').on('click', function() {
        loadModalActivityDemandData();
    });
    
    // 点击模态框外部关闭模态框
    $('#planProgressModal').on('click', function(e) {
        if (e.target === this) {
            $(this).removeClass('active');
        }
    });
    
    // 加载模态框活动需求数据
    function loadModalActivityDemandData() {
        var userId = localStorage.getItem("userId");
        if (!userId) {
            alert("请先登录");
            return;
        }
        
        // 显示加载状态
        $('#modalReportTableBody').html('<tr><td colspan="9" style="text-align: center; padding: 20px;">加载中...</td></tr>');
        $('#modalNoDataMsg').hide();
        
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
                        $('#modalReportTableBody').empty();
                        $('#modalNoDataMsg').show();
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
                                $('#modalReportTableBody').empty();
                                $('#modalNoDataMsg').show();
                                return;
                            }
                            renderModalReportData(parsedResponse);
                        } catch (e) {
                            alert("获取数据失败：" + response);
                            $('#modalReportTableBody').empty();
                            $('#modalNoDataMsg').show();
                        }
                    } else {
                        // 检查是否是错误响应
                        if (response.flag === 'fail' || response.flag === 'error') {
                            // 只显示data部分的内容
                            alert(response.data || "获取数据失败");
                            $('#modalReportTableBody').empty();
                            $('#modalNoDataMsg').show();
                            return;
                        }
                        alert("获取数据失败：" + JSON.stringify(response));
                        $('#modalReportTableBody').empty();
                        $('#modalNoDataMsg').show();
                    }
                }
            },
            error: function(xhr, status, error) {
                alert("获取数据失败，请检查网络连接或稍后重试。错误信息：" + error + "\n状态码：" + xhr.status);
                $('#modalReportTableBody').empty();
                $('#modalNoDataMsg').show();
            }
        });
    }
    
    // 渲染模态框报表数据
    function renderModalReportData(data) {
        var reportListBody = $('#modalReportListBody');
        var noDataMsg = $('#modalNoDataMsg');
        
        reportListBody.empty();
        
        // 检查数据是否存在
        if (!data || !data.data || data.data.length === 0) {
            noDataMsg.show();
            return;
        }
        
        noDataMsg.hide();
        
        // 遍历数据，生成活动项
        data.data.forEach(function(item) {
            console.log('活动项数据:', item);
            // 活动ID - 必须是数字，否则无法取消活动
            var demandId = item.demandId || item.id || item.demand_id || 0;
            if (!demandId || demandId === 0) {
                console.warn('活动项缺少有效的demandId:', item);
                return; // 跳过没有ID的活动项
            }
            
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
            
            // 状态 - 使用后端返回的statusText（可能是驼峰或下划线命名）
            var statusText = item.statusText || item.status_text || '未知';
            var statusClass = 'status-pending';
            var activityProgress = parseInt(item.activity_progress || item.activityProgress || item.activity_progress || 0);
            console.log('activity_progress值:', item.activity_progress, 'activityProgress值:', item.activityProgress, 'status值:', item.status, '最终使用:', activityProgress);
            console.log('statusText:', statusText);
            
            // 根据statusText设置样式类
            if (statusText.indexOf('拒绝') >= 0 || statusText.indexOf('取消') >= 0) {
                statusClass = 'status-rejected';
            } else if (statusText.indexOf('完成') >= 0 || statusText.indexOf('支付尾款') >= 0) {
                statusClass = 'status-completed';
            } else if (statusText.indexOf('通过') >= 0 || statusText.indexOf('支付定金') >= 0) {
                statusClass = 'status-approved';
            } else if (statusText.indexOf('待处理') >= 0) {
                statusClass = 'status-pending';
            } else {
                statusClass = 'status-processing';
            }
            
            // 拒绝理由
            var refuseReason = item.refuse_reason || item.refuseReason || '-';
            
            // 创建活动项
            var activityItem = $('<div class="activity-item"></div>');
            
            // 活动项头部
            var header = $('<div class="activity-item-header"></div>');
            var title = $('<div class="activity-item-title">' + activityPlace + ' ' + activityType + '</div>');
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
            var refuseReasonDiv = $('<div class="activity-item-time">拒绝理由: ' + refuseReason + '</div>');
            
            // 根据状态显示不同按钮
            var actionButtons = '';
            // 状态为3(人员安排完成)时显示支付定金按钮
            if (activityProgress === 3) {
                actionButtons += '<button class="activity-item-action btn-pay" onclick="payDeposit(' + demandId + ')">支付定金</button>';
            }
            // 状态为0,1,2,3时可以取消活动
            if (activityProgress >= 0 && activityProgress <= 3) {
                actionButtons += '<button class="activity-item-action btn-cancel" onclick="cancelActivity(' + demandId + ')">取消活动</button>';
            }
            
            footer.append(refuseReasonDiv, actionButtons);
            
            // 组装活动项
            activityItem.append(header, content, desc, footer);
            
            // 添加到列表
            reportListBody.append(activityItem);
        });
    }
    
    // 显示活动详情
    function showActivityDetail(item) {
        // 填充详情数据
        $('#detailDemandId').text(item.demandId || item.id || '未知');
        $('#detailActivityType').text(item.activity_type || item.activityType || '未知');
        $('#detailActivityPlace').text(item.activity_place || item.activityPlace || '未知');
        
        var startTime = item.start_time || item.startTime || '';
        var endTime = item.end_time || item.endTime || '';
        var timeText = startTime && endTime ? startTime + ' 至 ' + endTime : '未知';
        $('#detailActivityTime').text(timeText);
        
        $('#detailParticipantCount').text(item.activity_people || item.participantCount || '未知');
        $('#detailBudget').text(item.activity_budget || item.budget || '未知');
        $('#detailDescription').text(item.requirement_desc || item.demandDescription || item.description || '无');
        
        // 状态显示 - 优先使用后端返回的statusText
        var statusText = item.statusText || '未知';
        var activityProgress = item.activity_progress || item.status || 0;
        
        // 如果没有statusText，则根据activityProgress判断
        if (!item.statusText) {
            switch (activityProgress) {
                case 0:
                case '0':
                    statusText = '待处理';
                    break;
                case 1:
                case '1':
                    statusText = '业务管理员通过';
                    break;
                case 2:
                case '2':
                    statusText = '仓库管理员通过';
                    break;
                case 3:
                case '3':
                    statusText = '人员安排完成';
                    break;
                case 4:
                case '4':
                    statusText = '用户已支付定金';
                    break;
                case 5:
                case '5':
                    statusText = '用户已支付尾款';
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
        }
        $('#detailStatus').text(statusText);
        
        $('#detailSubmitTime').text(item.create_time || item.submitTime || '未知');
        
        // 显示详情模态框
        $('#activityDetailModal').addClass('active');
    }
    
    // 活动详情模态框关闭按钮点击事件
    $('#activityDetailClose').on('click', function() {
        $('#activityDetailModal').removeClass('active');
    });
    
    // 活动详情模态框取消按钮点击事件
    $('#activityDetailCancelBtn').on('click', function() {
        $('#activityDetailModal').removeClass('active');
    });
    
    // 点击活动详情模态框外部关闭模态框
    $('#activityDetailModal').on('click', function(e) {
        if (e.target === this) {
            $(this).removeClass('active');
        }
    });
    
    // 初始化轮播图
    initCarousel();
});

// 页面滚动效果
window.addEventListener('scroll', function() {
    const navbar = document.querySelector('.navbar');
    if (window.scrollY > 50) {
        navbar.style.backgroundColor = 'rgba(255, 255, 255, 0.95)';
        navbar.style.boxShadow = '0 2px 10px rgba(0, 0, 0, 0.15)';
    } else {
        navbar.style.backgroundColor = '#fff';
        navbar.style.boxShadow = '0 2px 8px rgba(0, 0, 0, 0.1)';
    }
});

// 数字动画效果（当页面滚动到统计区域时触发）
function animateNumbers() {
    const stats = document.querySelectorAll('.stat-number');
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                const stat = entry.target;
                const target = parseInt(stat.textContent.replace(/[^0-9]/g, ''));
                const duration = 2000;
                const step = target / (duration / 16);
                let current = 0;
                
                const timer = setInterval(() => {
                    current += step;
                    if (current >= target) {
                        current = target;
                        clearInterval(timer);
                    }
                    
                    if (stat.textContent.includes('+')) {
                        stat.textContent = Math.floor(current) + '+';
                    } else if (stat.textContent.includes('°')) {
                        stat.textContent = Math.floor(current) + '°';
                    } else {
                        stat.textContent = Math.floor(current);
                    }
                }, 16);
                
                observer.unobserve(stat);
            }
        });
    }, { threshold: 0.5 });
    
    stats.forEach(stat => {
        observer.observe(stat);
    });
}

// 初始化数字动画
if ('IntersectionObserver' in window) {
    animateNumbers();
}

// 登录模态框功能
const headerLoginBtn = document.getElementById('headerLoginBtn');
const loginModal = document.getElementById('loginModal');
const modalClose = document.getElementById('modalClose');
const modalLoginForm = document.getElementById('modalLoginForm');
const modalRegisterForm = document.getElementById('modalRegisterForm');
const modalTitle = document.getElementById('modalTitle');
const loginFormContainer = document.getElementById('loginFormContainer');
const registerFormContainer = document.getElementById('registerFormContainer');
const switchToRegisterBtn = document.getElementById('registerBtn');
const loginSubmitBtn = document.getElementById('loginBtn');
const submitRegisterBtn = document.getElementById('submitRegisterBtn');
const backToLoginBtn = document.getElementById('backToLoginBtn');
const registerPassword = document.getElementById('registerPassword');
const registerConfirmPassword = document.getElementById('registerConfirmPassword');
const passwordError = document.querySelector('.password-error');

// 检查passwordError是否存在
if (!passwordError) {
    console.error('未找到密码错误提示元素');
}

// 打开登录模态框
headerLoginBtn.addEventListener('click', function() {
    loginModal.classList.add('active');
    // 确保显示的是登录表单
    showLoginForm();
    // 聚焦到用户名输入框
    setTimeout(() => {
        document.getElementById('modalUsername').focus();
    }, 300);
});

// 切换到注册表单
switchToRegisterBtn.addEventListener('click', function() {
    // 调用login.js中定义的showRegisterForm函数
    showRegisterForm();
    // 聚焦到名称输入框
    setTimeout(() => {
        document.getElementById('registerName').focus();
    }, 300);
});

// 切换回登录表单
backToLoginBtn.addEventListener('click', function() {
    // 调用login.js中定义的showLoginForm函数
    showLoginForm();
});

// 关闭登录模态框
function closeModal() {
    // 检查用户是否已登录，如果未登录才允许关闭
    var user = localStorage.getItem("user");
    var isLoggedIn = false;
    if(user) {
        try {
            var userData = JSON.parse(user);
            if(userData && userData.name && userData.userId) {
                isLoggedIn = true;
            }
        } catch(e) {
            // JSON解析错误，视为未登录
        }
    }
    
    // 只有未登录状态才允许关闭模态框
    if(!isLoggedIn) {
        loginModal.classList.remove('active');
        // 重置并显示登录表单
        showLoginForm();
    } else {
        alert("请先退出登录！");
    }
}

// 点击关闭按钮关闭模态框
modalClose.addEventListener('click', closeModal);

// 移除点击模态框外部关闭模态框的逻辑
// 移除按ESC键关闭模态框的逻辑
// 现在模态框只能通过右上角的关闭按钮关闭

// 登录表单提交处理
modalLoginForm.addEventListener('submit', function(e) {
    e.preventDefault();
    const username = document.getElementById('modalUsername').value;
    const password = document.getElementById('modalPassword').value;
    
    // 这里可以添加登录逻辑
    console.log('登录信息:', {
        username: username,
        password: password
    });
    
    // 注意：真正的登录和注册逻辑在login.js中处理
// 这里不再自动关闭模态框，由login.js控制
})

// 注册表单密码验证（保留用于实时密码验证）
registerConfirmPassword.addEventListener('input', function() {


    if (!passwordError) {
        return;
    }
    
    const password = registerPassword.value;
    const confirmPassword = this.value;
    
    if (password !== confirmPassword) {
        passwordError.style.display = 'block';
        this.setCustomValidity('两次输入的密码不一致');
    } else {
        passwordError.style.display = 'none';
        this.setCustomValidity('');
    }
})

// 注意：注册表单提交处理已移至login.js
// 这里不再处理注册表单提交，避免与login.js中的逻辑冲突

// 取消活动
function cancelActivity(demandId) {
    if (!confirm('确定要取消这个活动吗？取消后无法恢复！')) {
        return;
    }
    
    $.ajax({
        url: '../CancelActivity',
        type: 'POST',
        data: {
            demandId: demandId
        },
        success: function(response) {
            if (response.flag === 'success') {
                alert('活动已取消！');
                // 刷新活动列表
                loadMyActivityProgress();
            } else {
                alert('取消失败：' + (response.data || '未知错误'));
            }
        },
        error: function(xhr, status, error) {
            console.error('取消活动失败:', error);
            alert('取消活动失败，请稍后重试');
        }
    });
}

// 支付定金
function payDeposit(demandId) {
    if (!confirm('确定要支付定金吗？')) {
        return;
    }
    
    $.ajax({
        url: '../PayActivityDeposit',
        type: 'POST',
        data: {
            demandId: demandId
        },
        success: function(response) {
            if (response.flag === 'success') {
                alert('定金支付成功！');
                // 刷新活动列表
                loadMyActivityProgress();
            } else {
                alert('支付失败：' + (response.data || '未知错误'));
            }
        },
        error: function(xhr, status, error) {
            console.error('支付定金失败:', error);
            alert('支付定金失败，请稍后重试');
        }
    });
}