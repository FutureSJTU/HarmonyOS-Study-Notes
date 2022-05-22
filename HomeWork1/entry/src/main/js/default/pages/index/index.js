import prompt from'@system.prompt';

export default {
    data: {
        Image1: '/common/images/玫瑰.jpg',
        Image2: '/common/images/菊花.jpg',
        fruit1: '/common/images/橘子.jpg',
        fruit2: '/common/images/苹果.jpg',
        fruit3: '/common/images/葡萄.jpg',
        fruit4: '/common/images/西瓜.jpg',
        fruit5: '/common/images/香蕉.jpg',
        detailImage: '/common/detail.png',
        colorParam: '',
        opacityParam: '',
        textList: [{value: 'JS FA'}],
    },
    showDialog(e) {
        this.$element('simpledialog').show()
    },
    change(e) {
        if (e.index == 1)
        this.$element('simpledialog').show()
        else if (e.index == 0) {
            this.colorParam = '';
            this.opacityParam = '';
            this.colorParam = 'color';
            this.opacityParam = 'opacity';
        }
    },
    setSchedule(e) {
        this.$element('simpledialog').close()
        prompt.showToast({
            message: '确认'
        })
    },
    cancelDialog(e) {
        prompt.showToast({
            message: '离开'
        })
    },
    cancelSchedule(e) {
        this.$element('simpledialog').close()
        prompt.showToast({
            message: '确认取消'
        })
    },

}