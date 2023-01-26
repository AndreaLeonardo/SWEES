
/* USER CODE BEGIN Header */
/**
  ******************************************************************************
  * @file           : main.c
  * @brief          : Main program body
  ******************************************************************************
  * @attention
  *
  * Copyright (c) 2022 STMicroelectronics.
  * All rights reserved.
  *
  * This software is licensed under terms that can be found in the LICENSE file
  * in the root directory of this software component.
  * If no LICENSE file comes with this software, it is provided AS-IS.
  *
  ******************************************************************************
  */
/* USER CODE END Header */
/* Includes ------------------------------------------------------------------*/
#include "main.h"
#include "cmsis_os.h"
/* Private includes ----------------------------------------------------------*/
/* USER CODE BEGIN Includes */
#include "scheduler.h"
#include "stdio.h"

/* USER CODE END Includes */

/* Private typedef -----------------------------------------------------------*/
/* USER CODE BEGIN PTD */

/* USER CODE END PTD */

/* Private define ------------------------------------------------------------*/
/* USER CODE BEGIN PD */
/* USER CODE END PD */

/* Private macro -------------------------------------------------------------*/
/* USER CODE BEGIN PM */

/* USER CODE END PM */

/* Private variables ---------------------------------------------------------*/
UART_HandleTypeDef huart2;

osThreadId defaultTaskHandle;

/* USER CODE BEGIN PV */
SemaphoreHandle_t mbx;
SemaphoreHandle_t mux1;
SemaphoreHandle_t mux2;
/* USER CODE END PV */

/* Private function prototypes -----------------------------------------------*/
void SystemClock_Config(void);
static void MX_GPIO_Init(void);
static void MX_USART2_UART_Init(void);

/* USER CODE BEGIN PFP */

/* USER CODE END PFP */

/* Private user code ---------------------------------------------------------*/
/* USER CODE BEGIN 0 */
int _write(int file, char *ptr, int len)
{
  /* Implement your write code here, this is used by puts and printf for example */
  int i=0;
  for(i=0 ; i<len ; i++)
    ITM_SendChar((*ptr++));
  return len;
}

#define INCREMENT_ON_TICK 4180
#define BASE_I_VALUE 2069
#define SPORADIC_DELAY 120

int waitTimesTask1[1] = {5 * INCREMENT_ON_TICK + BASE_I_VALUE};
int waitTimesTask2[2] = {1 * INCREMENT_ON_TICK + BASE_I_VALUE, 1 * INCREMENT_ON_TICK + BASE_I_VALUE};
int waitTimesTask3[3] = {1 * INCREMENT_ON_TICK + BASE_I_VALUE, 5 * INCREMENT_ON_TICK + BASE_I_VALUE, 1 * INCREMENT_ON_TICK + BASE_I_VALUE};
int waitTimesTask4[3] = {1 * INCREMENT_ON_TICK + BASE_I_VALUE, 5 * INCREMENT_ON_TICK + BASE_I_VALUE, 1 * INCREMENT_ON_TICK + BASE_I_VALUE};
int waitTimesTask5[2] = {1 * INCREMENT_ON_TICK + BASE_I_VALUE, 1 * INCREMENT_ON_TICK + BASE_I_VALUE};

int iterTask1 = 0;
int iterTask2 = 0;
int iterTask3 = 0;
int iterTask4 = 0;
int iterTask5 = 0;

int timeToFire = 0;

TaskHandle_t xHandle1 = NULL;
TaskHandle_t xHandle2 = NULL;
TaskHandle_t xHandle3 = NULL;
TaskHandle_t xHandle4 = NULL;
TaskHandle_t xHandle5 = NULL;

int lastExecTime = 0;

static void taskS1( int *delayArray ){
	printf("t20\n%d\n", xTaskGetTickCount() - timeToFire);
	timeToFire = xTaskGetTickCount();
	vTaskPrioritySet(NULL,1);
	int i = 0;
	iterTask5 = 0;
	int randomDelay = (rand() % 1) * INCREMENT_ON_TICK;
	delayArray[iterTask5] += randomDelay;
	while(i < delayArray[iterTask5])
		i++;
	delayArray[iterTask5] -= randomDelay;
	printf("t21\n%d\n", xTaskGetTickCount() - timeToFire);
	timeToFire = xTaskGetTickCount();

	vTaskPrioritySet(NULL,3);
	printf("t22\n%d\n", xTaskGetTickCount() - timeToFire);
	timeToFire = xTaskGetTickCount();

	while(xSemaphoreTake(mux1,1) == pdFALSE){}
	printf("t23\n%d\n", xTaskGetTickCount() - timeToFire);
	timeToFire = xTaskGetTickCount();

	i = 0;
	iterTask5 += 1;
	randomDelay = (rand() % 1) * INCREMENT_ON_TICK;
	delayArray[iterTask5] += randomDelay;
	while(i < delayArray[iterTask5])
		i++;
	delayArray[iterTask5] -= randomDelay;

	printf("t24\n%d\n", xTaskGetTickCount() - timeToFire);
	timeToFire = xTaskGetTickCount();
	xSemaphoreGive(mux1);

	vTaskPrioritySet(NULL,1);
}

static void task1( int *delayArray ){
	printf("t0\n%d\n", xTaskGetTickCount() - timeToFire);
	timeToFire = xTaskGetTickCount();

	while(xSemaphoreTake(mbx,1) == pdFALSE){}

	printf("t1\n%d\n", xTaskGetTickCount() - timeToFire);
	timeToFire = xTaskGetTickCount();
	int i = 0;
	iterTask1 = 0;
	int randomDelay = (rand() % 5) * INCREMENT_ON_TICK;
	delayArray[iterTask1] += randomDelay;
	while(i < delayArray[iterTask1])
		i++;
	delayArray[iterTask1] -= randomDelay;
	printf("t2\n%d\n", xTaskGetTickCount() - timeToFire);
	timeToFire = xTaskGetTickCount();
}

static void task2( int *delayArray ){
	printf("t3\n%d\n", xTaskGetTickCount() - timeToFire);
	timeToFire = xTaskGetTickCount();
	vTaskPrioritySet(NULL,4);
	int i = 0;
	iterTask2 = 0;
	int randomDelay = (rand() % 1) * INCREMENT_ON_TICK;
	delayArray[iterTask2] += randomDelay;
	while(i < delayArray[iterTask2])
		i++;
	delayArray[iterTask2] -= randomDelay;

	printf("t4\n%d\n", xTaskGetTickCount() - timeToFire);
	timeToFire = xTaskGetTickCount();

	xSemaphoreGive(mbx);

	i = 0;
	iterTask2 += 1;
	randomDelay = (rand() % 1) * INCREMENT_ON_TICK;
	delayArray[iterTask2] += randomDelay;
	while(i < delayArray[iterTask2])
		i++;
	delayArray[iterTask2] -= randomDelay;
	printf("t5\n%d\n", xTaskGetTickCount() - timeToFire);
	timeToFire = xTaskGetTickCount();
	vTaskPrioritySet(NULL,5);
}

static void task3( int *delayArray ){
	printf("t6\n%d\n", xTaskGetTickCount() - timeToFire);
	timeToFire = xTaskGetTickCount();
	vTaskPrioritySet(NULL,3);
	while(xSemaphoreTake(mux1,1) == pdFALSE){}
	printf("t7\n%d\n", xTaskGetTickCount() - timeToFire);
	timeToFire = xTaskGetTickCount();
	int i = 0;
	iterTask3 = 0;
	int randomDelay = (rand() % 1) * INCREMENT_ON_TICK;
	delayArray[iterTask3] += randomDelay;
	while(i < delayArray[iterTask3])
		i++;
	delayArray[iterTask3] -= randomDelay;

	printf("t8\n%d\n", xTaskGetTickCount() - timeToFire);
	timeToFire = xTaskGetTickCount();

	xSemaphoreGive(mux1);

	i = 0;
	iterTask3 += 1;
	randomDelay = (rand() % 5) * INCREMENT_ON_TICK;
	delayArray[iterTask3] += randomDelay;
	while(i < delayArray[iterTask3])
		i++;
	delayArray[iterTask3] -= randomDelay;
	printf("t9\n%d\n", xTaskGetTickCount() - timeToFire);
	timeToFire = xTaskGetTickCount();
	while(xSemaphoreTake(mux2,1) == pdFALSE){}
	printf("t10\n%d\n", xTaskGetTickCount() - timeToFire);
	timeToFire = xTaskGetTickCount();

	i = 0;
	iterTask3 += 1;
	randomDelay = (rand() % 1) * INCREMENT_ON_TICK;
	delayArray[iterTask3] += randomDelay;
	while(i < delayArray[iterTask3])
		i++;
	delayArray[iterTask3] -= randomDelay;

	printf("t11\n%d\n", xTaskGetTickCount() - timeToFire);
	timeToFire = xTaskGetTickCount();

	xSemaphoreGive(mux2);
	vTaskPrioritySet(NULL,5);
}

static void task4( int *delayArray ){
	printf("t12\n%d\n", xTaskGetTickCount() - timeToFire);
	timeToFire = xTaskGetTickCount();
	vTaskPrioritySet(NULL,2);
	printf("t13\n%d\n", xTaskGetTickCount() - timeToFire);
	timeToFire = xTaskGetTickCount();

	while(xSemaphoreTake(mux1,1) == pdFALSE){}
	printf("t14\n%d\n", xTaskGetTickCount() - timeToFire);
	timeToFire = xTaskGetTickCount();
	vTaskPrioritySet(NULL,3);
	int i = 0;
	iterTask4 = 0;
	int randomDelay = (rand() % 1) * INCREMENT_ON_TICK;
	delayArray[iterTask4] += randomDelay;
	while(i < delayArray[iterTask4])
		i++;
	delayArray[iterTask4] -= randomDelay;

	printf("t15\n%d\n", xTaskGetTickCount() - timeToFire);
	timeToFire = xTaskGetTickCount();

	xSemaphoreGive(mux1);

	vTaskPrioritySet(NULL,2);

	i = 0;
	iterTask4 += 1;
	randomDelay = (rand() % 5) * INCREMENT_ON_TICK;
	delayArray[iterTask4] += randomDelay;
	while(i < delayArray[iterTask4])
		i++;
	delayArray[iterTask4] -= randomDelay;
	printf("t16\n%d\n", xTaskGetTickCount() - timeToFire);
	timeToFire = xTaskGetTickCount();


	vTaskPrioritySet(NULL,3);

	printf("t17\n%d\n", xTaskGetTickCount() - timeToFire);
		timeToFire = xTaskGetTickCount();

	while(xSemaphoreTake(mux2,1) == pdFALSE){}

	printf("t18\n%d\n", xTaskGetTickCount() - timeToFire);
	timeToFire = xTaskGetTickCount();

	i = 0;
	iterTask4 += 1;
	randomDelay = (rand() % 1) * INCREMENT_ON_TICK;
	delayArray[iterTask4] += randomDelay;
	while(i < delayArray[iterTask4])
		i++;
	delayArray[iterTask4] -= randomDelay;

	printf("t19\n%d\n", xTaskGetTickCount() - timeToFire);
	timeToFire = xTaskGetTickCount();

	xSemaphoreGive(mux2);

	vTaskPrioritySet(NULL,5);
}


static void task5(int *delayArray){
	if(xTaskGetTickCount() - lastExecTime >= SPORADIC_DELAY){
		if(rand() % 300 == 0){
			BaseType_t xReturnValue = xSchedulerSporadicJobCreate( taskS1, "S1", delayArray, pdMS_TO_TICKS( 0 ), pdMS_TO_TICKS( 120 ) );
			if( pdTRUE == xReturnValue ) {
				lastExecTime = xTaskGetTickCount();
			}
		}
	}
}
/* USER CODE END 0 */

/**
  * @brief  The application entry point.
  * @retval int
  */
int main(void){
  /* USER CODE BEGIN 1 */
	mbx = xSemaphoreCreateCounting(1,0);
	mux1 = xSemaphoreCreateCounting(1,1);
	mux2 = xSemaphoreCreateCounting(1,1);
  /* USER CODE END 1 */

  /* MCU Configuration--------------------------------------------------------*/

  /* Reset of all peripherals, Initializes the Flash interface and the Systick. */
  HAL_Init();

  /* USER CODE BEGIN Init */

  /* USER CODE END Init */

  /* Configure the system clock */
  SystemClock_Config();

  srand(0);

  /* USER CODE BEGIN SysInit */

  /* USER CODE END SysInit */

  /* Initialize all configured peripherals */
  MX_GPIO_Init();
  MX_USART2_UART_Init();
  /* USER CODE BEGIN 2 */
  //printf( " TASK,EVENT,TIME\n" );

  	vSchedulerInit();
  	//Phase, Period, WCET, Deadline
  	vSchedulerPeriodicTaskCreate(task1, "task1", configMINIMAL_STACK_SIZE, waitTimesTask1, 5, &xHandle1, pdMS_TO_TICKS(40), pdMS_TO_TICKS(40), pdMS_TO_TICKS(0), pdMS_TO_TICKS(40));
  	vSchedulerPeriodicTaskCreate(task2, "task2", configMINIMAL_STACK_SIZE, waitTimesTask2, 5, &xHandle2, pdMS_TO_TICKS(40), pdMS_TO_TICKS(40), pdMS_TO_TICKS(0), pdMS_TO_TICKS(40));
  	vSchedulerPeriodicTaskCreate(task3, "task3", configMINIMAL_STACK_SIZE, waitTimesTask3, 5, &xHandle3, pdMS_TO_TICKS(80), pdMS_TO_TICKS(80), pdMS_TO_TICKS(0), pdMS_TO_TICKS(80));
  	vSchedulerPeriodicTaskCreate(task4, "task4", configMINIMAL_STACK_SIZE, waitTimesTask4, 5, &xHandle3, pdMS_TO_TICKS(100), pdMS_TO_TICKS(100), pdMS_TO_TICKS(0), pdMS_TO_TICKS(100));
  	vSchedulerPeriodicTaskCreate(task5, "task5", configMINIMAL_STACK_SIZE, waitTimesTask5, 5, &xHandle5, pdMS_TO_TICKS(SPORADIC_DELAY), pdMS_TO_TICKS(10), pdMS_TO_TICKS(0), pdMS_TO_TICKS(10));

  	vSchedulerStart();

  /* USER CODE END RTOS_THREADS */

  /* Start scheduler */


  /* We should never get here as control is now taken by the scheduler */
  /* Infinite loop */
  /* USER CODE BEGIN WHILE */
  while (1)
  {
    /* USER CODE END WHILE */

    /* USER CODE BEGIN 3 */
  }
  /* USER CODE END 3 */
}

/**
  * @brief System Clock Configuration
  * @retval None
  */
void SystemClock_Config(void)
{
  RCC_OscInitTypeDef RCC_OscInitStruct = {0};
  RCC_ClkInitTypeDef RCC_ClkInitStruct = {0};

  /** Configure the main internal regulator output voltage
  */
  __HAL_RCC_PWR_CLK_ENABLE();
  __HAL_PWR_VOLTAGESCALING_CONFIG(PWR_REGULATOR_VOLTAGE_SCALE2);

  /** Initializes the RCC Oscillators according to the specified parameters
  * in the RCC_OscInitTypeDef structure.
  */
  RCC_OscInitStruct.OscillatorType = RCC_OSCILLATORTYPE_HSI;
  RCC_OscInitStruct.HSIState = RCC_HSI_ON;
  RCC_OscInitStruct.HSICalibrationValue = RCC_HSICALIBRATION_DEFAULT;
  RCC_OscInitStruct.PLL.PLLState = RCC_PLL_ON;
  RCC_OscInitStruct.PLL.PLLSource = RCC_PLLSOURCE_HSI;
  RCC_OscInitStruct.PLL.PLLM = 16;
  RCC_OscInitStruct.PLL.PLLN = 336;
  RCC_OscInitStruct.PLL.PLLP = RCC_PLLP_DIV4;
  RCC_OscInitStruct.PLL.PLLQ = 7;
  if (HAL_RCC_OscConfig(&RCC_OscInitStruct) != HAL_OK)
  {
    Error_Handler();
  }

  /** Initializes the CPU, AHB and APB buses clocks
  */
  RCC_ClkInitStruct.ClockType = RCC_CLOCKTYPE_HCLK|RCC_CLOCKTYPE_SYSCLK
                              |RCC_CLOCKTYPE_PCLK1|RCC_CLOCKTYPE_PCLK2;
  RCC_ClkInitStruct.SYSCLKSource = RCC_SYSCLKSOURCE_PLLCLK;
  RCC_ClkInitStruct.AHBCLKDivider = RCC_SYSCLK_DIV1;
  RCC_ClkInitStruct.APB1CLKDivider = RCC_HCLK_DIV2;
  RCC_ClkInitStruct.APB2CLKDivider = RCC_HCLK_DIV1;

  if (HAL_RCC_ClockConfig(&RCC_ClkInitStruct, FLASH_LATENCY_2) != HAL_OK)
  {
    Error_Handler();
  }
}

/**
  * @brief USART2 Initialization Function
  * @param None
  * @retval None
  */
static void MX_USART2_UART_Init(void)
{

  /* USER CODE BEGIN USART2_Init 0 */

  /* USER CODE END USART2_Init 0 */

  /* USER CODE BEGIN USART2_Init 1 */

  /* USER CODE END USART2_Init 1 */
  huart2.Instance = USART2;
  huart2.Init.BaudRate = 115200;
  huart2.Init.WordLength = UART_WORDLENGTH_8B;
  huart2.Init.StopBits = UART_STOPBITS_1;
  huart2.Init.Parity = UART_PARITY_NONE;
  huart2.Init.Mode = UART_MODE_TX_RX;
  huart2.Init.HwFlowCtl = UART_HWCONTROL_NONE;
  huart2.Init.OverSampling = UART_OVERSAMPLING_16;
  if (HAL_UART_Init(&huart2) != HAL_OK)
  {
    Error_Handler();
  }
  /* USER CODE BEGIN USART2_Init 2 */

  /* USER CODE END USART2_Init 2 */

}

/**
  * @brief GPIO Initialization Function
  * @param None
  * @retval None
  */
static void MX_GPIO_Init(void)
{
  GPIO_InitTypeDef GPIO_InitStruct = {0};

  /* GPIO Ports Clock Enable */
  __HAL_RCC_GPIOC_CLK_ENABLE();
  __HAL_RCC_GPIOH_CLK_ENABLE();
  __HAL_RCC_GPIOA_CLK_ENABLE();
  __HAL_RCC_GPIOB_CLK_ENABLE();

  /*Configure GPIO pin Output Level */
  HAL_GPIO_WritePin(LD2_GPIO_Port, LD2_Pin, GPIO_PIN_RESET);

  /*Configure GPIO pin : B1_Pin */
  GPIO_InitStruct.Pin = B1_Pin;
  GPIO_InitStruct.Mode = GPIO_MODE_IT_FALLING;
  GPIO_InitStruct.Pull = GPIO_NOPULL;
  HAL_GPIO_Init(B1_GPIO_Port, &GPIO_InitStruct);

  /*Configure GPIO pin : LD2_Pin */
  GPIO_InitStruct.Pin = LD2_Pin;
  GPIO_InitStruct.Mode = GPIO_MODE_OUTPUT_PP;
  GPIO_InitStruct.Pull = GPIO_NOPULL;
  GPIO_InitStruct.Speed = GPIO_SPEED_FREQ_LOW;
  HAL_GPIO_Init(LD2_GPIO_Port, &GPIO_InitStruct);

}

/* USER CODE BEGIN 4 */

/* USER CODE END 4 */

/* USER CODE BEGIN Header_StartDefaultTask */
/**
  * @brief  Function implementing the defaultTask thread.
  * @param  argument: Not used
  * @retval None
  */
/* USER CODE END Header_StartDefaultTask */

/**
  * @brief  Period elapsed callback in non blocking mode
  * @note   This function is called  when TIM1 interrupt took place, inside
  * HAL_TIM_IRQHandler(). It makes a direct call to HAL_IncTick() to increment
  * a global variable "uwTick" used as application time base.
  * @param  htim : TIM handle
  * @retval None
  */
void HAL_TIM_PeriodElapsedCallback(TIM_HandleTypeDef *htim)
{
  /* USER CODE BEGIN Callback 0 */

  /* USER CODE END Callback 0 */
  if (htim->Instance == TIM1) {
    HAL_IncTick();
  }
  /* USER CODE BEGIN Callback 1 */

  /* USER CODE END Callback 1 */
}

/**
  * @brief  This function is executed in case of error occurrence.
  * @retval None
  */
void Error_Handler(void)
{
  /* USER CODE BEGIN Error_Handler_Debug */
  /* User can add his own implementation to report the HAL error return state */
  __disable_irq();
  while (1){}
  /* USER CODE END Error_Handler_Debug */
}

#ifdef  USE_FULL_ASSERT
/**
  * @brief  Reports the name of the source file and the source line number
  *         where the assert_param error has occurred.
  * @param  file: pointer to the source file name
  * @param  line: assert_param error line source number
  * @retval None
  */
void assert_failed(uint8_t *file, uint32_t line)
{
  /* USER CODE BEGIN 6 */
  /* User can add his own implementation to report the file name and line number,
     ex: printf("Wrong parameters value: file %s on line %d\r\n", file, line) */
  /* USER CODE END 6 */
}
#endif /* USE_FULL_ASSERT */
