<template>
  <div class="new-process-page">
    <div class="page-title">新建流程</div>

    <div class="process-grid">
      <div v-for="item in processList" :key="item.id" class="process-card" @click="handleSelect(item)">
        <div class="card-icon" :style="{ background: item.bg }">
          <span class="icon-text">{{ item.icon }}</span>
        </div>
        <div class="card-body">
          <div class="card-title">{{ item.title }}</div>
          <div class="card-desc">{{ item.desc }}</div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup>
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'

const router = useRouter()

const processList = [
  { id: 1, title: '费用报销单', desc: '指报销申请单据', icon: '💰', bg: 'linear-gradient(135deg, #00a65a, #28b868)' },
  { id: 2, title: '出差申请单', desc: '指出差申请单', icon: '🚗', bg: 'linear-gradient(135deg, #409eff, #6ab0ff)' },
  { id: 3, title: '加班申请单', desc: '指加班申请单', icon: '⏰', bg: 'linear-gradient(135deg, #409eff, #7ec2ff)' },
  { id: 4, title: '转正申请单', desc: '指转正申请单', icon: '📋', bg: 'linear-gradient(135deg, #409eff, #6ab0ff)' },
  { id: 5, title: '请假申请单', desc: '指请假申请单', icon: '🏃', bg: 'linear-gradient(135deg, #17b3a3, #46d5c5)' },
  { id: 6, title: '离职申请单', desc: '指离职申请单', icon: '🚶', bg: 'linear-gradient(135deg, #333, #666)' }
]

function handleSelect(item) {
  const routes = {
    1: '/process/expense',
    2: '/process/trip',
    3: '/attendance/overtime-form',
    4: '/process/regular',
    5: '/attendance/leave-form',
    6: '/process/resignation'
  }
  if (routes[item.id]) router.push(routes[item.id])
  else ElMessage.info(`"${item.title}"功能开发中`)
}
</script>

<style scoped lang="scss">
.new-process-page {
  .page-title {
    font-size: 18px;
    font-weight: 600;
    color: #333;
    margin-bottom: 24px;
  }

  .process-grid {
    display: grid;
    grid-template-columns: repeat(3, 1fr);
    gap: 20px;

    .process-card {
      display: flex;
      align-items: center;
      gap: 16px;
      background: #fff;
      border-radius: 10px;
      padding: 24px 20px;
      box-shadow: 0 2px 8px rgba(0,0,0,0.06);
      cursor: pointer;
      transition: all 0.25s;

      &:hover {
        box-shadow: 0 4px 16px rgba(0,0,0,0.12);
        transform: translateY(-2px);
      }

      .card-icon {
        width: 56px;
        height: 56px;
        border-radius: 12px;
        display: flex;
        align-items: center;
        justify-content: center;
        flex-shrink: 0;

        .icon-text {
          font-size: 24px;
        }
      }

      .card-body {
        flex: 1;
        min-width: 0;

        .card-title {
          font-size: 16px;
          font-weight: 500;
          color: #333;
          margin-bottom: 4px;
        }

        .card-desc {
          font-size: 13px;
          color: #999;
        }
      }
    }
  }
}
</style>
