![Screenshot_2024-08-12-21-43-41-114_com example ponggame](https://github.com/user-attachments/assets/0afa7577-d566-4990-bd52-5314cf45f38e)# Pong Game

A modern twist on the classic Pong game, developed using Kotlin for Android. This version includes multiple balls, ball speed increments, a scoring system, and a custom paddle, providing a challenging and engaging experience.

## Features

- **Multi-Ball Gameplay:** The game introduces new balls every 5 successful bounces, up to a maximum of 3 balls on screen.
- **Scoring System:** Track your score based on the number of times the ball hits the paddle.
- **Dynamic Difficulty:** Ball speed increases as you progress, adding to the challenge.
- **Custom Paddle:** The paddle has a gradient color and rounded edges for a modern look.
- **Game Over and Restart:** If any ball misses the paddle, the game ends and displays a "Game Over" screen with an option to restart.

## Screenshots
![Screenshot_2024-08-12-21-43-41-114_com example ponggame](https://github.com/user-attachments/assets/7085ba02-97c4-43f5-8ed4-555c1152d51c)

![Game Screenshot 1](link_to_screenshot_1)
![Game Screenshot 2](link_to_screenshot_2)

## How to Play

1. **Start the Game:** Launch the game, and the first ball will appear on the screen.
2. **Control the Paddle:** Drag your finger horizontally on the screen to move the paddle and bounce the ball.
3. **Score Points:** Every time the ball hits the paddle, your score increases.
4. **Multiple Balls:** After every 5 successful bounces, a new ball will appear, up to 3 balls.
5. **Game Over:** The game ends if any ball goes below the paddle. Tap anywhere on the screen to restart.

## Installation

1. Clone the repository:
   ```bash
   git clone https://github.com/yourusername/pong-game.git
   ```
2. Open the project in Android Studio.
3. Build and run the project on an emulator or Android device.

## Code Overview

### Ball Class
The `Ball` data class manages the properties and behaviors of the balls, including position, speed, and radius.

### Paddle Control
The paddle is controlled using touch events, allowing the player to move it left or right to bounce the balls.

### Collision Detection
The game uses precise collision detection to determine when a ball hits the paddle or the walls, adjusting the ball's trajectory accordingly.

### Scoring and Difficulty
The game tracks the score and dynamically increases the difficulty by adding more balls and increasing their speed over time.

### Game Over and Restart
When a ball is missed, the game displays a "Game Over" screen and allows the player to tap the screen to restart the game.

## Customization

You can easily modify various aspects of the game, such as:
- **Number of Balls:** Adjust the `maxBalls` variable to change the maximum number of balls.
- **Ball Speed:** Modify the `speedX` and `speedY` properties in the `Ball` class for different difficulty levels.
- **Paddle Size:** Change the `paddleWidth` and `paddleHeight` variables to customize the paddle size.

## Contributions

Contributions are welcome! If you have any ideas or suggestions for improving the game, feel free to submit a pull request or open an issue.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
